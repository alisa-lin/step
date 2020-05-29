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

/** Fetches existing comments and updates UI. */
function getComments() {
    fetch('/data').then(response => response.json()).then(comment => {
        if (!comment) { 
            console.log('null comment or no comment');
            return; 
        }
        console.log(comment);
        document.getElementById('comments').appendChild(createListElement(formatComment(comment)));
    });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Formats a comment as name: comment given the JSON comment. */
function formatComment(comment) {
  var json = JSON.parse(comment);
  return json.name + ": " + json.text;
}
