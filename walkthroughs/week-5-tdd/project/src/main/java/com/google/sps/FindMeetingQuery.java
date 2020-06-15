// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public final class FindMeetingQuery {

    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        System.err.println("New request!");
        ArrayList<TimeRange> possibleTimes = new ArrayList<TimeRange>();

        long requestDuration = request.getDuration();
        // if the duration is longer than the day, there is no possible meeting time
        if (TimeRange.WHOLE_DAY.duration() < requestDuration) {
            return possibleTimes;
        }

        possibleTimes.add(TimeRange.WHOLE_DAY);

        Collection<String> attendees = request.getAttendees();
        // if there are no attendees in the request, then the whole day is a possible meeting time
        if (attendees.isEmpty()) {
            return possibleTimes;
        }

        // check if there is attendee overlap
        ArrayList<Event> relevantEvents = new ArrayList<Event>();

        Collection<String> curAtt;
        Collection<String> tempCollection;
        for (Event cur : events) {
            curAtt = cur.getAttendees();
            tempCollection = attendees.stream()
                .filter(curAtt::contains)
                .collect(Collectors.toList());
            if (!tempCollection.isEmpty()) {
                relevantEvents.add(cur);
            }
        }

        // if none of the existing events have attendee overlap, then the whole day is a possible meeting time
        if (relevantEvents.isEmpty()) {
            return possibleTimes;
        }

        // check if there is time overlap
        TimeRange curPT = possibleTimes.get(0);
        TimeRange curRE = relevantEvents.get(0).getWhen();
        boolean remove = false; // if the current possible time needs to be removed/replaced
        boolean repBeg = false; // replace, beginning is the same
        boolean repEnd = false; // replace, end is the same
        int PTStart = 0;
        int REStart = 0;
        int removeIndexIncrement = 0;
        do {
            remove = false;
            repBeg = false;
            repEnd = false;
            outer:
            for (int i = PTStart; i < possibleTimes.size(); i++) {
                TimeRange posTime = possibleTimes.get(i);
                inner:
                for (int j = REStart; j < relevantEvents.size(); j++) {
                    if (REStart != 0 && i != PTStart) {
                        REStart = 0;
                    }
                    TimeRange evTime = relevantEvents.get(j).getWhen();
                    System.err.println("i is " + i + " and j is " + j);
                    System.err.println("posTime is " + posTime + " and evTime is " + evTime);
                    if (posTime.overlaps(evTime)) {
                        curPT = posTime;
                        curRE = evTime;
                        if (evTime.contains(posTime)) {
                            remove = true;
                            PTStart = i;
                            break outer;
                        } else if (posTime.contains(evTime)) {
                            repEnd = true;
                            repBeg = true;
                            remove = true;
                            PTStart = i;
                            REStart = j + 1;
                            break outer;
                        } else if (posTime.start() >= evTime.start()) {
                            repEnd = true;
                            remove = true;
                            PTStart = i;
                            REStart = j + 1;
                            break outer;
                        } else if (evTime.start() >= posTime.start()) {
                            repBeg = true;
                            remove = true;
                            PTStart = i;
                            REStart = j + 1;
                            break outer;
                        }
                    }
                }
            }
            if (repEnd) {
                System.err.println("adding " + TimeRange.fromStartEnd(curRE.end(), curPT.end() - 1, true) + " at index " + PTStart);
                possibleTimes.add(PTStart, TimeRange.fromStartEnd(curRE.end(), curPT.end() - 1, true));
                removeIndexIncrement++;
            }
            if (repBeg) {
                // if both repEnd and repBeg are true, this should displace repEnd by one.
                System.err.println("adding " + TimeRange.fromStartEnd(curPT.start(), curRE.start(), false) + " at index " + PTStart);
                possibleTimes.add(PTStart, TimeRange.fromStartEnd(curPT.start(), curRE.start(), false));
                removeIndexIncrement++;
            }
            if (remove) {
                System.err.println("removing " + possibleTimes.get(PTStart + removeIndexIncrement) + " at index " + PTStart + removeIndexIncrement);
                possibleTimes.remove(PTStart + removeIndexIncrement);
                removeIndexIncrement = 0;
            }
        } while (remove || repBeg || repEnd);

        if (!possibleTimes.isEmpty()) {
            int start = 0;
            // remove options that are shorter than the request duration
            do {
                remove = false;
                for (int i = start; i < possibleTimes.size(); i++) {
                    if (possibleTimes.get(i).duration() < requestDuration) {
                        System.err.println("Oops, " + possibleTimes.get(i) + " has duration " + possibleTimes.get(i).duration() + " and the request has duration " + request.getDuration());
                        remove = true;
                        start = i;
                        break;
                    }
                }
                if (remove) {
                    possibleTimes.remove(start);
                    if (start == possibleTimes.size()) {
                        remove = false;
                    }
                }
            } while (remove);
        }

        System.err.println("Done!");
        System.err.println();
        return possibleTimes;
    }
}
