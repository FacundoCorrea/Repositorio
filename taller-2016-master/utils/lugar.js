/**
 * Created by Emerson on 25/7/2016.
 */
var pg = require('pg');
var connectionString = process.env.DATABASE_URL || 'postgres://localhost:5433/taller';
var client = new pg.Client(connectionString);
client.connect();
