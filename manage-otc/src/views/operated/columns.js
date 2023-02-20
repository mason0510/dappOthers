
export const infoColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '序号',
    type: 'index',
    width: 60,
    align: 'center'
  },
  {
    title: '标题',
    align: 'center',
    width: 500,
    key: 'title'
  },
  {
    title: '类型',
    align: 'center',
    key: 'type'
  },
/*  {
    title: '来源',
    align: 'center',
    key: 'source'
  },*/
  {
    title: '关键词',
    align: 'center',
    key: 'keywords'
  },
  {
    title: '地域',
    align: 'center',
    render: (h, params) => {
        const row = params.row;
        const text = row.country +'----'+row.region;
        return text
    }
  },
  {
    title: '创建日期',
    align: 'center',
    render: (h, params) => {
        const row = params.row;
        return new Date(row.createdAt.time).toLocaleDateString();
    }
  },
  {
    title: '热门推荐',
    align: 'center',
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
    /*render: (h, params) => {
      const row = params.row;
      const color = row.status === 1 ? 'blue' : row.status === 2 ? 'green' : 'red';
      const text = row.status === 1 ? '构建中' : row.status === 2 ? '构建完成' : '构建失败';
      return h('Tag', {
        props: {
          type: 'dot',
          color: color
        }
      }, text);
    }*/
  },
  {
    title: '轮播推荐',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return h('a', {
        on: {
          'click': () => {
            row.recommend=!row.recommend;
          }
        }
      }, row.recommend?"已推荐":"未推荐")
    }
  },
/*  {
    title: '热推顺序',
    align: 'center',
    key: 'hotOrder'
  },*/
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
];
export const wikiColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: 'ID',
    width: 80,
    align: 'center',
    key:'bkId'
  },
  {
    title: '标题',
    align: 'center',
    width: 400,
    key: 'title'
  },
  {
    title: '类型',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return wikiType[row.bigType];
    }
  },
/*  {
    title: '来源',
    align: 'center',
    key: 'source'
  },*/
  {
    title: '关键词',
    align: 'center',
    key: 'keywords'
  },
  {
    title: '地域',
    align: 'center',
    render: (h, params) => {
        const row = params.row;
        const text = row.country +'----'+row.region;
        return text
    }
  },
  {
    title: '创建日期',
    align: 'center',
    render: (h, params) => {
        const row = params.row;
        return new Date(row.createdAt.time).toLocaleDateString();
    }
  },
  {
    title: '热门推荐',
    align: 'center',
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
    /*render: (h, params) => {
      const row = params.row;
      const color = row.status === 1 ? 'blue' : row.status === 2 ? 'green' : 'red';
      const text = row.status === 1 ? '构建中' : row.status === 2 ? '构建完成' : '构建失败';
      return h('Tag', {
        props: {
          type: 'dot',
          color: color
        }
      }, text);
    }*/
  },
  {
    title: '轮播推荐',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return h('a', {
        on: {
          'click': () => {
            row.recommend=!row.recommend;
          }
        }
      }, row.recommend?"已推荐":"未推荐")
    }
  },
/*  {
    title: '热推顺序',
    align: 'center',
    key: 'hotOrder'
  },*/
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
];
export const newHouseColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '',
    type: 'index',
    width: 60,
    align: 'center'
  },
  {
    title: '楼盘名称',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      let buildingType='';
      switch (row.buildingType){
        case 0:
          buildingType='独立屋'
          break;
        case 1:
          buildingType='排屋'
          break;
        case 2:
          buildingType='公寓'
          break;
        default:
          break;
      }

      return row.nameEnglish + '/' + row.nameChinese + '(' + buildingType + ')';
    }
  },
  {
    title: '城市',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      const text = row.cityZH +'('+row.city + ')';
      return text
    }
  },
  {
    title: '销售状态',
    width: 120,
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      let text = '';
      switch (row.status){
        case 1:
          text='现房';
          break;
        case 2:
          text='在售';
          break;
        case 3:
          text='售罄';
          break;
        case 4:
          text='预售';
          break;
        case 5:
          text='现售';
          break;
        default:
          break;
      }

      let color = "green";
      if(row.publishStatus!=1){
        color = "yellow";
      }

      return h('Tag', {
        props: {
          type: 'dot',
          color: color
        }
      }, text);
      return text
    }
  },
  {
    title: '操作',
    align: 'center',
    width: 200,
    key: 'handle',
    handle: [{text:'图片',type:1},'edit', 'delete']
  }
];
export const residenceColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '房屋编号',
    align: 'center',
    key:'mslno'
  },
  {
    title: '城市',
    align: 'center',
    key:'city'
  },
  {
    title: '英文',
    align: 'center',
    width:500,
    ellipsis: true,
    key:'descEn'
  },
  {
    title: '中文',
    align: 'center',
    width:500,
    ellipsis: true,
    key:'descZh'
  },
  {
    title: '创建日期',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return new Date(row.createdAt.time).toLocaleDateString();
    }
  },
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
/*  {
    title: '操作',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return h('a', {
        on: {
          'click': () => {
            //row.publishStatus=!row.publishStatus;
          }
        }
      }, '翻译')
    }
  }*/
];
export const tranColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '类型',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return row.type==1?'内容':'其他';
    }
  },
  {
    title: '英文',
    align: 'center',
    width:500,
    ellipsis: true,
    key:'source'
  },
  {
    title: '中文',
    align: 'center',
    width:500,
    ellipsis: true,
    key:'result'
  },
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
];
export const bannerColumns = [
  {
    title: '序号',
    type: 'index',
    width: 70,
    align: 'center'
  },
  {
    title: '国家',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return row.country=='ca'?'加拿大':'美国';
    }
  },
  {
    title: '位置',
    align: 'center',
    render: (h, params) => {
      const row = params.row;
      return row.position==1?'首页顶部':'首页中部';
    }
  },
  {
    title: '顺序',
    align: 'center',
    ellipsis: true,
    key:'order'
  },
  {
    title: '链接',
    align: 'center',
    width:500,
    ellipsis: true,
    key:'url'
  },
  {
    title: '链接类型',
    align: 'center',
    ellipsis: true,
    key:'urlType'
  },
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
];
export const labelColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '标签名',
    align: 'center',
    key:'name'
  },
  {
    title: '对应数量',
    align: 'center',
    key:'countNum'
  },
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
];
export const seoColumns = [
  {
    type: 'selection',
    width: 60,
    align: 'center'
  },
  {
    title: '名称',
    width: 80,
    align: 'center',
    key:'name'
  },
  {
    title: '标题',
    align: 'center',
    key:'title'
  },
  {
    title: '关键字',
    align: 'center',
    key:'keyword'
  },
  {
    title: '描述',
    align: 'center',
    key:'description'
  },
  {
    title: '操作',
    align: 'center',
    key: 'handle',
    handle: ['edit', 'delete']
  }
];

export const wikiType = {
  1100:'城市概况',
  1200:'购房准备',
  1300:'交易环节',
  1400:'热门百科'
};

const tableColumns = {
  infoColumns: infoColumns,
  wikiColumns: wikiColumns,
  newHouseColumns: newHouseColumns,
  residenceColumns: residenceColumns,
  tranColumns: tranColumns,
  bannerColumns: bannerColumns,
  labelColumns: labelColumns,
  seoColumns: seoColumns,
};
export default tableColumns;
