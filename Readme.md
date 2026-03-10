update the groq api at application.properties =
groq.api.key = ENTER_YOUR_GROQ_API_HERE 

using TpmsApplication


// use this curl to direct upload the resume directly to the ai-agent groq 
postman request POST 'http://localhost:8080/api/resumes/ai/upload' 'file=@"/C:/Users/SaurabhSharma/Downloads/saurabh-sharma-resume-updated (1).pdf"'

// USE THIS CURL TO UPLOAD THE RESUME AT THE APACHE TIKA
postman request POST 'http://localhost:8080/api/resumes/upload' 'file=@"/C:/Users/SaurabhSharma/Downloads/saurabh-sharma-resume-updated (1).pdf"'

// login
postman request POST 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--body '{
"username":"kala",
"password": "kala@1234",
"name" : "kala_chawal"
}'

// signup
postman request POST 'http://localhost:8080/api/auth/signup' \
--header 'Content-Type: application/json' \
--body '{
"username":"kala",
"password": "kala@1234",
"name" : "kala_chawal"
}'

// get user details
postman request 'http://localhost:8080/api/resumes/profile/{Id}'


