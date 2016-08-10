var express = require('express');
var morgan = require('morgan');
var pgClient = require('../../../utils/database_connection');
var router = express.Router();

// localhost/blablabla?lng=123&lat=123
router.get('/', function (req, res) {
  var client = pgClient.connect();
  var queryString = 'SELECT id, nombre, ST_AsGeoJSON(punto) AS location FROM lugares as p ' +
    'WHERE ST_DWithin(p.punto, ' +
    'Geography(ST_MakePoint($1, $2)), ' +
    '100);';
  var query = client.query(queryString, [req.query.lng, req.query.lat]);
  query.on('end', function (result) {
    var points = result.rows;

    for(var i=0; i<points.length; i++){
      points[i].location = JSON.parse(points[i]['location']);
    }

    res.send(points);
  });
});
// localhost/blabla/123/details
// localhost/blabla/321/details
// donde 123 y 321 = :id
router.get('/:id/details', function (req, res) {
  var client = pgClient.connect();
  var queryString = 'SELECT id, description, ST_AsGeoJSON(punto) AS location   ' +
    'FROM lugares ' +
    'WHERE id = $1;';
  var query = client.query(queryString, [req.params.id]);
  query.on('end', function (result) {
    client.end();
    var point = result.rows[0];
    point.location = JSON.parse(point['location']);
    res.send(point);
  });
});
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
router.get('/:id/cantidadLugares', function (req, res) {
  console.log(req, res);
  var client = pgClient.connect();
  var querystring =' select count(*) from checks where idUsuario =$1' ;
  var query = client.query(querystring, [req.params.id]);
  query.on('end', function (result) {
    client.end();
      var resultado =result;
    res.send(resultado.rows);
  });
});
//localhost/idUsuario/idLugar/check
router.get('/:idUsuario/:idLugar/check', function (req, res) {
  console.log(req, res);
  var client = pgClient.connect();
  function toTimestamp(year,month,day,hour,minute,second){
    var datum = new Date(Date.UTC(year,month-1,day,hour,minute,second));
    return datum.getTime()/1000;
  }
  var fecha =toTimestamp();
  var querystring ='insert into checks (idUsuario,idLugar,fecha) values ($1,$2,$3)' ;
  var query = client.query(querystring,[req.params.idUsuario ,req.params.idLugar, fecha]);
  query.on('end', function (result) {
    client.end();
    res.send(result.rows);
  });
});
module.exports = router;
