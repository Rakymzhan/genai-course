# genai-course

Task 1:
To run task 1 set next VM options:
    -DAZURE_OPEN_AI_DEPLOYMENT_NAME=gpt-4o
    -DAZURE_OPEN_AI_KEY=${your-api-key}
    -DAZURE_OPEN_AI_ENDPOINT=https://ai-proxy.lab.epam.com

cURL examples:
    Plain text response: curl --location 'http://localhost:8088/api/books/topN/sample1?input=I%20want%20to%20find%20top-10%20books%20about%20world%20history'
    JSON response: curl --location 'http://localhost:8088/api/books/topN/sample2?input=I%20want%20to%20find%20top-10%20books%20about%20world%20history'
