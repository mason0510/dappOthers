import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;


export const getSystemLogList = params => { return axios.post(`${base}/log/log-rest/search-system-log`, params); };

export const getRequestLogList = params => { return axios.post(`${base}/log/log-rest/search-request-log`, params); };
