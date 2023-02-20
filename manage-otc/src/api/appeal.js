import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;
//let base = "http://127.0.0.1:9600";

//申述列表
export const appealList = params => {
	return axios({
		method:'post',
		url:`${base}/otc/otc-rest/m/user-appeal-list`,
		data:params
	})
};

//申诉信息详情
export const appealInfo = params => {
  return axios({
    method:'get',
    url:`${base}/otc/otc-rest/m/user-appeal-info`,
    data:params
  })
};

//审核申诉
export const appealCheck = params => {
  return axios({
    method:'post',
    url:`${base}/otc/otc-rest/m/user-appeal-check`,
    data:params
  })
};
