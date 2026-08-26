**ChatGPT Java Client**

A lightweight Java client for interacting with the OpenAI Chat Completions API. Sends a prompt, retrieves the model's response, and reports token usage — with optional logging of conversations to a MySQL database.

**Features**

Send prompts to OpenAI's gpt-3.5-turbo-0125 model via HttpURLConnection
Parse the JSON response to extract the reply text and token usage
Optional persistence of prompts/responses/token counts to a MySQL database

**Prerequisites**

Java 11+
An OpenAI API key
(Optional) MySQL server, if using the database logging feature

**Setup**

**1. Clone the repository**
   
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>

**2. Configure your API key**

Do not hardcode your API key in the source. Set it as an environment variable instead:

export OPENAI_API_KEY="your-api-key-here"

Then update the code to read it:

String apiKey = System.getenv("OPENAI_API_KEY");

**3. (Optional) Configure the database**

If you want to log conversations, create a MySQL database and table:

sql
CREATE DATABASE chat_logs;

USE chat_logs;

CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prompt TEXT NOT NULL,
    response TEXT NOT NULL,
    tokens_used INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

Update the connection details in insertIntoDatabase (ideally also via environment variables rather than hardcoded credentials).

**Usage**

**Compile and run:**

javac src/a.java
java -cp src a

**Example:**

String response = chatGPT("Describe what a prime number is and give examples.");
System.out.println(response);

**Output:**

Response: A prime number is a natural number greater than 1 that has no positive divisors other than 1 and itself...
Tokens used: 47

**Project Structure**

src/
└── a.java   # Core client: request building, response parsing, optional DB logging

**Known Limitations**

Response parsing is string-based, not a proper JSON parser — it locates fields with indexOf/substring. This will break on multi-line responses, escaped quotes, or any change in OpenAI's response formatting. Consider using a JSON library such as org.json or Gson for a more robust implementation.

max_tokens is capped at 50, which will truncate longer responses.

The class name a and package src are placeholders — consider renaming to something descriptive (e.g. ChatGptClient, com.yourname.chatclient) for readability and to follow Java naming conventions.

No retry/error handling beyond wrapping IOException in a RuntimeException.

**Roadmap Ideas**

  Replace manual string parsing with a JSON library
  Support configurable model and max_tokens
  Add unit tests
  Move database credentials to environment variables / a config file
