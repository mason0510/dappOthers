import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');
let base = baseUrl;


export const getBannerList = params => { return axios.post(`${base}/system/banner/m/search`,params); };

export const saveBanner = params => { return axios.post(`${base}/system/banner/m/save`,params); };

export const getBannerById = params => { return axios.get(`${base}/system/banner/m/detail?id=` + params); };

export const deleteBanner = params => { return axios.post(`${base}/system/banner/m/delete`,qs.stringify(params)); };
