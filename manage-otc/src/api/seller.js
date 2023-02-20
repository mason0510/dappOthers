import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;
// let base = "http://127.0.0.1:8800";

//商户列表
export const sellerList = params => {
	return axios({
		method:'post',
		url:`${base}/otc/otc-rest/m/seller-list`,
    // url:`${base}/otc-rest/m/seller-list`,
		data:params
	})
};

//添加商户
export const addSeller = params => {
  return axios({
    method:'post',
    url:`${base}/otc/otc-rest/m/add-seller`,
    // url:`${base}/otc-rest/m/add-seller`,
    data:params
  })
};
