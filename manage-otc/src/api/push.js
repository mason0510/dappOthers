import axios from 'axios';
import {baseUrl} from './baseUrl'
var qs = require('qs');

let base = baseUrl;

export const getHistoryMsg = params => { 
	return axios({//推送历史列表
		method:'post',
		url:`${base}/message/message-rest/jpush-message-history-list`,
		data:params,
	})
};
export const jpushMessage = params => { 
	return axios({//发送消息
		method:'post',
		url:`${base}/message/message-rest/batch-send-jpush-message`,
		data:params,
	})
};
export const delMessage = params => { 
	return axios({//删除
		method:'post',
		url:`${base}/message/message-rest/del-jpush-message`,
		params:params,
	})
};
export const jpushList = params => { 
	return axios({//消息管理列表
		method:'post',
		url:`${base}/message/message-rest/jpush-message-list`,
		data:params,
	})
};
export const saveJpush = params => { 
	return axios({//保存
		method:'post',
		url:`${base}/message/message-rest/save-jpush-message`,
		params:params,
	})
};
export const managerSendJpush = params => { 
	return axios({//重新发送
		method:'post',
		url:`${base}/message/message-rest/manager/send-jpush-message`,
		data:params,
	})
};