const express = require('express')  

const app = express()  
const port = 9090
app.use(express.static(__dirname + '/main'));
app.set('views', __dirname + '/main');
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');

app.get('/', (request, response) => {
  response.render('index.html');
})

app.listen(port, (err) => {  
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})