import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;


export const getApplicationList = params => { return axios.get(`${base}/oauth/oauth-rest/all-application-list`); };
export const addApplication = params => { return axios.post(`${base}/oauth/oauth-rest/add-application`, params); };
export const updateApplication = params => { return axios.post(`${base}/oauth/oauth-rest/update-application`, params); };
