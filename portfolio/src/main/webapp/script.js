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

/** onload function that displays comments and comment form/login information */
function displayCommentSection() {
    getComments();
    fetch('/login').then(response => response.json()).then(info => handleCommentForm(info.loggedIn, info.redirectUrl, info.userEmail));
}

/** Given the login status, this function displays the comment form or the log in button */
function handleCommentForm(loggedIn, redirectUrl, userEmail) {
    var login = document.getElementById('login');
    if (!loggedIn) {
        document.getElementById('comments-form').style.display = "none";
        login.innerHTML = "Log in"
        login.href = redirectUrl;
    } else {
        document.getElementById('comments-form').style.display = "block";
        login.innerHTML = "Log out"
        login.href = redirectUrl;
    }
}

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
  titleElement.innerText = comment.email + ": " + comment.text;

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
