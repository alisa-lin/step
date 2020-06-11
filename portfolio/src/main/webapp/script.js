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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Creates a chart and adds it to the page. */
function drawChart() {
    console.log("drawing chart");
    var data = google.visualization.arrayToDataTable([
        ['App', 'Hours per Week'],
        ['Instagram',     6],
        ['Facebook',      4.8],
        ['Reddit',  4.6],
        ['Messenger', 3.75],
        ['Youtube',    3.25]
    ]);

    var options = {
        title: 'My Screentime',
        pieHole: 0.4,
    };

    var chart = new google.visualization.PieChart(document.getElementById('chart-container'));
    chart.draw(data, options);

/** Fetches existing comments and updates UI. */
function getComments() {
    document.getElementById('comments').innerHTML = "";
    var maxComments = document.getElementById("maxComments").value;
    fetch(`/data?max=${maxComments}`).then(response => response.json()).then(comments => {
        comments.forEach(function(comment) {
            document.getElementById('comments').appendChild(createComment(comment));
        })
    });
}

/** Creates a comment element. */
function createComment(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const titleElement = document.createElement('span');
  titleElement.innerText = comment.name + ": " + comment.text;

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(comment);

    // Remove the comment from the DOM.
    commentElement.remove();
  });

  commentElement.appendChild(titleElement);
  commentElement.appendChild(deleteButtonElement);
  return commentElement;
}

/** Tells the server to delete the given comment. */
function deleteComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-data', {method: 'POST', body: params});
}
