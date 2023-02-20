
export const qaColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '用户',
    width: 150,
    align: 'center',
    key:'nickName'
  },
  {
    title: '标题',
    width: 500,
    align: 'center',
    key:'title'
  },
  {
    title: '补充说明',
    width: 300,
    align: 'center',
    key:'remark'
  },
  {
    title: '区域',
    align: 'center',
    width:150,
    render: (h, params) => {
      const row = params.row;
      const text = row.country +'----'+row.region;
      return text
    }
  },
  {
    title: '提问时间',
    width:100,
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return new Date(row.createdAt.time).toLocaleDateString();
    }
  },
  {
    title: '审核状态',
    render: (h, params) => {
      const row = params.row;
      return h('a', {
        on: {
          'click': () => {
            row.status=!row.status;
          }
        }
      }, row.status?"已审核":"未审核")
    }
  },
  {
    title: '首页推荐',
    render: (h, params) => {
      const row = params.row;
      return h('a', {
        on: {
          'click': () => {
            row.hotRecommend=!row.hotRecommend;
          }
        }
      }, row.hotRecommend?"已推荐":"未推荐")
    }
  },
  {
    title: '轮播推荐',
    key: 'name',
    render: (h, params) => {
      const row = params.row;
      return h('a', {
        on: {
          'click': () => {
            row.bannerRecommend=!row.bannerRecommend;
          }
        }
      }, row.bannerRecommend?"已推荐":"未推荐")
    }
  },
  {
    title: '操作',
    align: 'center',
    width:100,
    key: 'handle',
    handle: ['delete']
  }
];


const tableColumns = {
  qaColumns: qaColumns,
};
export default tableColumns;
