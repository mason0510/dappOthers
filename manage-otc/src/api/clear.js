import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;
// let base = "http://127.0.0.1:8800";

//清空用户资金密码错误次数
export const clearCipherCount = params => {
  return axios({
    method:'post',
    url:`${base}/otc/otc-rest/m/clear-capital-cipher-err-count`,
    // url:`${base}/otc-rest/m/clear-capital-cipher-err-count`,
    data:params
  })
};

//清空用户订单取消次数
export const clearTradeCount = params => {
  return axios({
    method:'post',
    url:`${base}/otc/otc-rest/m/clear-trade-cancel-count`,
    // url:`${base}/otc-rest/m/clear-trade-cancel-count`,
    data:params
  })
};
