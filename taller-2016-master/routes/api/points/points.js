var express = require('express');
var morgan = require('morgan');
var pgClient = require('../../../utils/database_connection');
var router = express.Router();

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

router.get('/:id/details', function (req, res) {
  var client = pgClient.connect();
  var queryString = 'SELECT id, nombre, ST_AsGeoJSON(punto) AS location   ' +
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

module.exports = router;