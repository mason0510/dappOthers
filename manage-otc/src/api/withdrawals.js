import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;
// let base = "http://127.0.0.1:8800";

//用户提币订单列表
export const capitalOutList = params => {
	return axios({
		method:'post',
		url:`${base}/otc/otc-rest/m/capital-out-list`,
    // url:`${base}/otc-rest/m/capital-out-list`,
		data:params
	})
};

//执行发币
export const withdrawals = params => {
  return axios({
    method:'post',
    url:`${base}/otc/otc-rest/m/withdrawals`,
    // url:`${base}/otc-rest/m/withdrawals`,
    data:params
  })
};
