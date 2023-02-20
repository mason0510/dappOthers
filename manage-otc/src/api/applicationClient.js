import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;


export const getApplicationClientList = params => { return axios.get(`${base}/oauth/oauth-rest/application-client-list?applicationId=`+params); };
export const addApplicationClient = params => { return axios.post(`${base}/oauth/oauth-rest/add-application-client`, params); };
export const updateApplicationClient = params => { return axios.post(`${base}/oauth/oauth-rest/update-application-client`, params); };
