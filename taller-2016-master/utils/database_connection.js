var pg = require('pg');

module.exports = {
  connect: function () {
    var connectionString = process.env.DATABASE_URL || 'postgres://postgres:groot99@localhost:5433/postgis_22_sample';
    var client = new pg.Client(connectionString);
    client.connect();
    return client;
  },
  getNextId: function (client) {
    return client.query('select nextval(\'main_seq\')');
  }
};
