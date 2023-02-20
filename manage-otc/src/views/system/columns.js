var moment = require('moment')

export const applicationColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '序号',
    type: 'index',
    width: 80,
    align: 'center'
  },
  {
    title: '名称',
    align: 'center',
    key: 'name'
  },
  {
    title: '别名',
    align: 'center',
    key: 'nickName'
  },
  {
    title: '自动绑定',
    render: (h, params) => {
      const row = params.row;
      const color = row.isAutoBind === 1 ? 'green' : 'red';
      const text = row.isAutoBind === 1 ? '是' : '否';
      return h('Tag', {
        props: {
          type: 'dot',
          color: color
        }
      }, text);
    }
  },
  {
    title: '操作',
    align: 'center',
    width: 200,
    key: 'handle',
    handle: ['edit', 'delete']
  }
];

export const applicationClientColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '序号',
    type: 'index',
    width: 80,
    align: 'center'
  },
  {
    title: '别名',
    align: 'center',
    key: 'clientNickName'
  },
  {
    title: '秘钥',
    align: 'center',
    key: 'secret'
  },
  {
    title: '有效期',
    align: 'center',
    key: 'timeMillis'
  },
  {
    title: '类型',
    render: (h, params) => {
      const row = params.row;
      let color = 'white'
      if(row.clientType == 1){
        color='green'
      } else if (row.clientType == 2){
        color='yellow'
      }else if(row.clientType == 3){
        color='blue'
      }

      let text = ''
      if(row.clientType == 1){
        text='Web'
      } else if (row.clientType == 2){
        text='苹果'
      }else if(row.clientType == 3){
        text='安卓'
      }

      return h('Tag', {
        props: {
          type: 'dot',
          color: color
        }
      }, text);
    }
  },
  {
    title: '操作',
    align: 'center',
    width: 200,
    key: 'handle',
    handle: ['edit', 'delete']
  }
];


export const userColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '序号',
    type: 'index',
    width: 80,
    align: 'center'
  },
  {
    title: '用户名',
    align: 'center',
    key: 'userName'
  },
  {
    title: '手机',
    align: 'center',
    key: 'mobile'
  },
  {
    title: '邮件',
    align: 'center',
    key: 'email'
  },
  {
    title: '性别',
    align: 'center',
    width: 80,
    render: (h, params) => {
      const row = params.row;
      let text = ''
      if(row.gender == 0){
        text='未知'
      } else if (row.gender == 1){
        text='男'
      }else if(row.gender == 2){
        text='女'
      }
return h('p', text)
    }
  },
  {
    title: '时间',
    width: 120,
    render: (h, params) => {
      const row = params.row;
      let date = moment(new Date(row.createTime)).format("MM-DD HH:mm:ss");
return h('p', date)
    }
  },
  {
    title: '来源',
    width: 120,
     align: 'center',
   key:'userFrom'
  },
  {
    title: '操作',
    align: 'center',
    width: 140,
    key: 'handle',
    handle: ['view', 'delete']
  }
];

const tableColumns = {
  applicationColumns: applicationColumns,
  applicationClientColumns: applicationClientColumns,
  userColumns:userColumns
};
export default tableColumns;
