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

/**
 * Adds a random greeting to the page.
 */

document.addEventListener("DOMContentLoaded", getQuote())
document.addEventListener("DOMContentLoaded", getMessages())

async function getQuote(){
    fetch("/data").then(response => response.text()).then((quote) => {
        document.getElementById("quote-container").innerText = quote;
    });    
}

async function getMessages(){
    const list = document.getElementById("message-list");
    list.innerText = "";
    const language = document.getElementById("lang").value;

    const params = new URLSearchParams();
    params.append('languageCode', language);
    fetch("/messages?" + params.toString()).then(response => response.json()).then((messages) => {
        messages.forEach(function(item){
            const listElement = document.createElement("li");
            listElement.innerText = item;
            list.appendChild(listElement);
        })
    });
}
