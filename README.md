# genai-course

Task 1:
To run task 1 set next VM options:
    -DAZURE_OPEN_AI_DEPLOYMENT_NAME=gpt-4o
    -DAZURE_OPEN_AI_KEY=${your-api-key}
    -DAZURE_OPEN_AI_ENDPOINT=https://ai-proxy.lab.epam.com

cURL examples:
    Plain text response: curl --location 'http://localhost:8088/api/books/topN/sample1?input=I%20want%20to%20find%20top-10%20books%20about%20world%20history'
    JSON response: curl --location 'http://localhost:8088/api/books/topN/sample2?input=I%20want%20to%20find%20top-10%20books%20about%20world%20history'

Task 3:
To run task 1 set next VM option:
    -DOPEN_AI_KEY=${your-api-key}

cURL example:
curl --location 'http://localhost:8088/api/books/topN' \
--header 'Content-Type: application/json' \
--data '{
"prompt": "I want to find top-10 books about world history",
"param": {
"temperature": 0.5
}
}'

Comparisons of responses from different models are calculated in percent and included in response.

Some observations:
    1) Responses from different models with the same prompt and prompt settings can be different.
    2) Some versions of gpt-4o and llama support JsonSchemaResponseFormat, it's enough to set up PromptExecutionSettings
        properly, and they return correct JSON. Other versions don't support this, and it's necessary to add instructions
        in prompt, so that they return responses in JSON, otherwise they throw an exception.
    3) Some models don't return a clean JSON, there can be additional quotation marks and descriptions,
        so you need to clean strings like this and parse them yourself.
    4) The models that pretrained for specific tasks (for example, to generate images, write a code) return empty response
        or an error.
    5) Sometimes a few models return not expected JSON, for example, you instruct that yearOfPublsih is a string,
        but it returns integer.
    6) In most cases request works correctly for the given models. Rarely it throws exception due to incorrect json.
        Re-running the request helped me.
    7) All the highlighted problems emphasize that different models for the same task can respond differently,
        and it is necessary to make individual adjustments for each model.

Task 4:

Example cURLs:
curl --location 'http://localhost:8088/api/plugin/check/time' \
--header 'Content-Type: application/json' \
--data '{
"prompt": "What current date and time is it in my city?"
}'

curl --location 'http://localhost:8088/api/plugin/check/lamp' \
--header 'Content-Type: application/json' \
--data '{
"prompt": "Check state of the lamp in the bathroom. Turn it on if it'\''s off"
}'

curl --location 'http://localhost:8088/api/plugin/check/weather' \
--header 'Content-Type: application/json' \
--data '{
"prompt": "Give me weather forecast for a week"
}'

Task 6:
To run the task set next VM option:
-DOPEN_AI_KEY=${your-api-key}

There is a sample laptop price list in /resources/data/laptops.csv.
The service will use it if no file provided in the /add request body.
Creating embeddings and ingesting them into Qdrant can take a long time,
as this file contains almost 2200 records. Trim it if you want to speed up the ingestion process.
