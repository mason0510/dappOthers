import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

 let base = baseUrl;
// let base = "http://127.0.0.1:9600";
// let base = "http://115.231.223.139:9600";

//用户交易订单列表
export const capitalDetailList = params => {
	return axios({
		method:'post',
		url:`${base}/otc/otc-rest/m/user-capital-detail-list`,
		data:params
	})
};

//执行发币
export const executeCurrency = params => {
  return axios({
    method:'post',
    url:`${base}/otc/otc-rest/m/execute-currency`,
    data:params
  })
};
