import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;


export const getUserList = params => { return axios.post(`${base}/oauth/oauth-rest/user-application-list`, params); };
export const addUser = params => { return axios.post(`${base}/oauth/oauth-rest/add-user`, params); };
//export const updateUser = params => { return axios.post(`${base}/oauth/oauth-rest/update-application-client`, params); };
export const loginUser = params => { return axios.post(`${base}/oauth/oauth-rest/login`, params); };
export const logoutUser = params => { return axios.post(`${base}/oauth/oauth-rest/logout`, params); };

//绑定应用
export const bindApplication = params => { return axios.post(`${base}/oauth/oauth-rest/update-user-application`, params); };
export const userInfoDetail = params => { 
	return axios({//用户信息
		method:'get',
		url:`${base}/oauth/oauth-rest/user-info`,
		params:params
	})
};
export const brokerInfoDetail = params => { 
	return axios({//经纪人主页
		method:'get',
		url:`${base}/oauth/oauth-rest/broker-info`,
		params:params
	})
};
export const userlistaccount = params => { 
	return axios({//模糊查询
		method:'get',
		url:`${base}/oauth/oauth-rest/search-user-list-by-account`,
		params:params
	})
};
export const deleteComment = params => { 
	return axios({//删除评论
		method:'post',
		url:`${base}/oauth/oauth-rest/delete-comment`,
		params:params
	})
};
export const listFeedback = params => { 
	return axios({//意见反馈
		method:'post',
		url:`${base}/oauth/oauth-rest/manage/list-feedback`,
		data:params
	})
};
export const userCount = params => { 
	return axios({//userCount
		method:'post',
		url:`${base}/oauth/oauth-rest/user-count`,
		data:params
	})
};