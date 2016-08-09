var express = require('express');
var pgClient = require('../../../utils/database_connection');
var router = express.Router();

router.post('/', function (req, res, next) {
  console.log(req, res, next);
  var client = pgClient.connect();
  var queryGetId = pgClient.getNextId(client);
  queryGetId.on('end', function () {

    var query = client.query('SELECT * FROM points');
    query.on('end', function () {
      client.end();
      res.send({
        status: 'success'
      });
    });
  });
});
//si existe te devuelve 1 sino 0
router.get('/:id/existe', function (req, res) {
  console.log(req, res);
  var client = pgClient.connect();
  var querystring =' select count(*) from usuarios where id =$1' ;
  var query = client.query(querystring, [req.params.id]);
  query.on('end', function (result) {
    client.end();
    var resultado =result;
    res.send(resultado.rows);
  });
});

module.exports = router;
