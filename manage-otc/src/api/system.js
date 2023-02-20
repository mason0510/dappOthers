import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;


export const getBigRegionList = params => { return axios.get(`${base}/system/region/listBigRegion`); };

export const getCityList = params => { return axios.get(`${base}/system/region/listCity?region=` + params); };

export const listRegionByCountry = params => { return axios.get(`${base}/system/region/listRegionByCountry?country=` + params); };
// export const addApplication = params => { return axios.post(`${base}/oauth/oauth-rest/add-application`, params); };
// export const updateApplication = params => { return axios.post(`${base}/oauth/oauth-rest/update-application`, params); };
export const upload = params => { return axios.post(`${base}/system/file-upload/upload-pic-base64`,qs.stringify(params)); };
export const uploadInfo = params => { return axios.post(`${base}/system/file-upload/upload-pic-base64-json`,params); };

export const fileUploadUrl = base + '/system/file-upload/upload-pic/';
