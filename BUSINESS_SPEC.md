## Context
Our company provides its clients with files of various natures (documents, reports, exports). Before being accessible for download, these files must imperatively have been analyzed by an antivirus mechanism.

## Objective of the Exercise
We want you to design and develop a secure file management microservice, capable of:
- Receiving and storing files transmitted by users or third-party systems
- Ensuring that no file is served to an end user without having been previously scanned by an antivirus
- Allowing the download of validated files via a programmatic interface
The service must support numerous simultaneous users and handle files of very variable sizes.

## Technical Constraints
The application must imperatively fit into a Java / Spring Boot / React ecosystem.

## Expected Deliverables
- The source code hosted on a public Git repository (GitHub, GitLab, etc.), of a functional application capable of receiving and serving files, and delegating their analysis to an antivirus available via an API.
- A document** (README or equivalent) describing:
  - Your technical and architectural choices
  - The assumptions you have formulated
  - The improvement paths you would consider

## Important Instructions
You are free to use all the tools at your disposal, including generative artificial intelligence (we would like you to store in the project's GitHub repo the prompts used) — its use is encouraged. If certain elements seem ambiguous or incomplete to you, formulate your own hypotheses and document them. You can also contact us by return email, we will respond very quickly.

## Continuation of the Process
Your solution will be the starting point of a technical exchange during an interview with "Engineering Manager" and "Architecte".
This interview will be an opportunity to present your choices, discuss the compromises, and explore together the possible evolution paths.

There is no single "good" answer: we will be more attentive to your reasoning, the quality of your approach, and your ability to defend your decisions.

Do not hesitate to come back to us for any question. We wish you good reflection and are impatient to discover your solution.
