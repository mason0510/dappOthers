import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');
let base = baseUrl;


export const getReleaseList = params => { return axios.post(`${base}/system/release/search`,params); };

export const saveRelease = params => { return axios.post(`${base}/system/release/edit`,params); };

export const getReleaseById = params => { return axios.get(`${base}/system/release/detail?id=` + params); };

export const getReleaseLatest = params => { return axios.get(`${base}/system/release/getLatest?appVersion=${params.appVersion}&device=${params.device}&channel=${params.channel}`); };

export const deleteRelease = params => { return axios.post(`${base}/system/release/delete`,qs.stringify(params)); };
