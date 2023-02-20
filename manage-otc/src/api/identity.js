import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

 let base = baseUrl;
// let base = "http://127.0.0.1:9600";

export const identityList = params => {
	return axios({//认证列表
		method:'post',
		url:`${base}/otc/otc-rest/m/user-identity-list`,
		data:params
	})
};

export const authentication = params => {
  return axios({//审核实名认证
    method:'post',
    url:`${base}/otc/otc-rest/m/authentication`,
    data:params
  })
};
