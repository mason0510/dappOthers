<template>
	<div>
		<Row :gutter="10">
			<Card>
				<p slot="title" style="height: 40px;line-height: 35px">
          <span>
            <Select  v-model="status" placeholder="申述状态" style="width: 120px">
              <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </span>
          <span><Input v-model="orderNumber"   placeholder="请输入订单号" style="width: 200px;" /></span>
					<span><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
				</p>
        <yztz-table :columns="columns" :tableData="tableData" :loading="loading" :total="total" :pageSize="pageSize"
                    :showOP="false" @changePage="changePage" @editRow="editRow" @viewRow="viewRow"
                     @selectionChange=selectionChange @pageSizeChange="pageSizeChange">
        </yztz-table>
			</Card>
      <Modal v-model="viewModal" :title="modalTitle" :mask-closable="false" class-name="big-model">
        <Form :label-width="100" :model="editForm" ref="editForm" >
          <template v-if="editForm.appealUserId == editForm.buyUserId">
            <FormItem label="申述人">
              {{editForm.buyUserName }}
            </FormItem>
            <FormItem label="申述人电话">
              {{editForm.buyUserMobile }}
            </FormItem>
            <FormItem label="申述人邮箱">
              {{editForm.buyUserEmail }}
            </FormItem>
            <FormItem label="申述人类别">
              买家
            </FormItem>
            <FormItem label="被申述人">
              {{editForm.sellUserName }}
            </FormItem>
            <FormItem label="被申述人电话">
              {{editForm.sellUserMobile }}
            </FormItem>
            <FormItem label="被申述人邮箱">
              {{editForm.sellUserEmail }}
            </FormItem>
            <FormItem label="被申述人类别">
              卖家
            </FormItem>
          </template>
          <template v-else>
            <FormItem label="申述人">
              {{editForm.sellUserName }}
            </FormItem>
            <FormItem label="申述人电话">
              {{editForm.sellUserMobile }}
            </FormItem>
            <FormItem label="申述人邮箱">
              {{editForm.sellUserEmail }}
            </FormItem>
            <FormItem label="申述人类别">
              卖家
            </FormItem>
            <FormItem label="被申述人">
              {{editForm.buyUserName }}
            </FormItem>
            <FormItem label="被申述人电话">
              {{editForm.buyUserMobile }}
            </FormItem>
            <FormItem label="被申述人邮箱">
              {{editForm.buyUserEmail }}
            </FormItem>
            <FormItem label="被申述人类别">
              买家
            </FormItem>
          </template>
          <FormItem label="申诉状态">
            {{editForm.appealStatus | appealStatus}}
          </FormItem>
          <FormItem label="申诉类型">
            {{editForm.appealType | appealType}}
          </FormItem>
          <FormItem label="申诉理由">
            {{editForm.appealReason}}
          </FormItem>
          <FormItem label="申诉时间">
            {{editForm.appealTime | formatDate}}
          </FormItem>
          <FormItem label="订单号">
            {{editForm.orderNumber}}
          </FormItem>
          <FormItem label="商品id">
            {{editForm.commodityId}}
          </FormItem>
          <FormItem label="币种金额">
            {{editForm.currencyPrice}}
          </FormItem>
          <FormItem label="币种">
            {{editForm.kind}}
          </FormItem>
          <FormItem label="商品类别">
            {{editForm.commodityType | commodityType}}
          </FormItem>
          <FormItem label="交易价格">
            {{editForm.transactionAmount}}
          </FormItem>
          <FormItem label="交易量">
            {{editForm.transactionVolume}}
          </FormItem>
          <FormItem label="付款时间">
            {{editForm.payTime | formatDate}}
          </FormItem>
        </Form>
        <!--<div slot="footer">-->
          <!--<Button @click="handelCancel">取消</Button>-->
          <!--<Button type="primary" @click="doAuthentication(3)">提交</Button>-->
        <!--</div>-->
      </Modal>
		</Row>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
  import {appealList, appealInfo, appealCheck} from '../../api/appeal';
  import {formatDate} from '../../filters/date';
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
				//申述状态
				status :0,
				statusList: [{
					label: '全部',
					value: ''
				}, {
					label: '处理中',
					value: 0
				}, {
					label: '胜诉',
					value: 1
				}, {
					label: '败诉',
					value: 2
				}, {
					label: '取消 ',
					value: 3
				}],
        orderNumber:'',
				//详情
				viewModal:false,
        modalTitle:'',
				editForm:{},
				//table
				columns: [],
				loading: false,
				tableData: [],
				pageSize: 20,
				pageIndex: 1,
				total: 0,
        // //图片查看
        // showPreviewImage:false,
        // showPreviewImageUrl:''
			}
		},
		methods: {
			// 初始化信息
			getData() {
				this.loading = true;
				let para = {
          status: this.status,
          orderNumber: this.orderNumber,
          currentPage: this.pageIndex,
          pageSize: this.pageSize
				}
        appealList(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                this.columns = [
                  {
                    title: '',
                    type: 'index',
                    width: 60,
                    align: 'center'
                  },
                  {
                    title: '申诉人',
                    align: 'center',
                    width: 100,
                    key:'appealUserName'
                  },
                  {
                    title: '申诉人电话',
                    align: 'center',
                    width: 120,
                    render: (h, params) => {
                      const row = params.row;
                      let mobile = ''
                      if(row.appealUserId == row.buyUserId){
                        mobile = row.buyUserMobile
                      }else{
                        mobile = row.sellUserMobile
                      }
                      return h('p', mobile)
                    }
                  },
                  {
                    title: '申诉人类别',
                    align: 'center',
                    width: 100,
                    render: (h, params) => {
                      const row = params.row;
                      let type = ''
                      if(row.appealUserId == row.buyUserId){
                        type = '买家'
                      }else{
                        type = '卖家'
                      }
                      return h('p', type)
                    }
                  },
                  {
                    title: '被申诉人',
                    align: 'center',
                    width: 100,
                    render: (h, params) => {
                      const row = params.row;
                      let username = ''
                      if(row.appealUserId == row.buyUserId){
                        username = row.sellUserName
                      }else{
                        username = row.buyUserName
                      }
                      return h('p', username)
                    }
                  },
                  {
                    title: '被申诉人电话',
                    align: 'center',
                    width: 120,
                    render: (h, params) => {
                      const row = params.row;
                      let mobile = ''
                      if(row.appealUserId == row.buyUserId){
                        mobile = row.sellUserMobile
                      }else{
                        mobile = row.buyUserMobile
                      }
                      return h('p', mobile)
                    }
                  },
                  {
                    title: '申诉状态',
                    width: 100,
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      let status = ''
                      switch (row.appealStatus) {
                        case 0:
                          status = '处理中'
                          break
                        case 1:
                          status = '胜诉'
                          break
                        case 2:
                          status = '败诉'
                          break
                        case 3:
                          status = '取消'
                          break
                        default:
                          break;
                      }
                      return h('p', status)
                    }
                  },
                  {
                    title: '申诉类型',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      let type = ''
                      switch (row.appealType) {
                        case 1:
                          type = '对方未付款'
                          break
                        case 2:
                          type = '对方未放行'
                          break
                        case 3:
                          type = '对方无应答'
                          break
                        case 4:
                          type = '对方有欺诈行为'
                          break
                        case 5:
                          type = '其他'
                          break
                        default:
                          break;
                      }
                      return h('p', type)
                    }
                  },
                  {
                    title: '申诉理由',
                    align: 'left',
                    width: 120,
                    key:'appealReason'
                  },
                  {
                    title: '订单号',
                    align: 'center',
                    width: 120,
                    key:'orderNumber'
                  },
                  /*{
                    title: '币种',
                    align: 'center',
                    width: 100,
                    key:'kind'
                  },
                  {
                    title: '交易价格',
                    align: 'center',
                    width: 100,
                    key:'transactionAmount'
                  },
                  {
                    title: '交易量',
                    align: 'center',
                    width: 100,
                    key:'transactionVolume'
                  },
                  {
                    title: '付款时间',
                    align: 'center',
                    width: 100,
                    render: (h, params) => {
                      const row = params.row;
                      let strDate = ''
                      strDate = new Date(row.payTime).toLocaleDateString()
                      return h('p', strDate)
                    }
                  },*/
                  {
                    title: '操作',
                    align: 'center',
                    // width: 250,
                    render: (h, params) => {
                      const row = params.row;
                      if (row.appealStatus == 0) {
                        return h('div', [
                          h('Button', {
                            style: {
                              margin: '0 5px'
                            },
                            props: {
                              type: 'primary',
                              size: 'small',
                              placement: 'bottom'
                            },
                            on: {
                              'click': () => {
                                this.viewRow(row, params.index);
                              }
                            }
                          }, '查看'),
                          h('Poptip', {
                            props: {
                              confirm: true,
                              title: '您确定' + row.buyUserName + '胜吗?',
                              transfer: true
                            },
                            on: {
                              'on-ok': () => {
                                this.appealCheck(row, 1, params.index);
                              }
                            }
                          }, [
                            h('Button', {
                              style: {
                                margin: '0 5px'
                              },
                              props: {
                                type: 'primary',
                                size: 'small',
                                placement: 'bottom'
                              }
                            }, row.buyUserName + '胜')
                          ]),
                          h('Poptip', {
                            props: {
                              confirm: true,
                              title: '您确定' + row.sellUserName + '胜吗?',
                              transfer: true
                            },
                            on: {
                              'on-ok': () => {
                                this.appealCheck(row, 2, params.index);
                              }
                            }
                          }, [
                            h('Button', {
                              style: {
                                margin: '0 5px'
                              },
                              props: {
                                type: 'error',
                                size: 'small',
                                placement: 'bottom'
                              }
                            }, row.sellUserName + '胜')
                          ])
                        ]);
                      }
                      else{
                        return h('div', [h('Button', {
                          style: {
                            margin: '0 5px'
                          },
                          props: {
                            type: 'primary',
                            size: 'small',
                            placement: 'bottom'
                          },
                          on: {
                            'click': () => {
                              this.viewRow(row, params.index);
                            }
                          }
                        }, '查看')
                        ]);
                      }
                    }
                  }
                ];
                this.tableData = res.data.data;
              } else {
                this.tableData = [];
              }

              this.total = res.data.totalResults;
            } else {
              if (res.data.resultMsg) {
                this.$Message.error(res.data.resultMsg);
              }
            }
          } else {
            this.$Message.error('接口读取失败!');
          }
          this.loading = false;
        });
			},

			// Component Add-Panel
			handleSearch(keywords) {
				this.pageIndex=1;
				this.getData();
			},
			// 表格内容
			//表格行 选中后变化
			selectionChange(selection) {
				alert(JSON.stringify(selection));
			},
			// 查看
			viewRow(row, index) {
				this.viewModal=true;
				this.editForm=Object.assign({}, row);
			},
      //编辑
      editRow(row, index){

      },
			// 表格翻页
			changePage(index) {
				// 这里直接更改了模拟的数据，真实使用场景应该从服务端获取数据
				this.pageIndex = index;
				this.getData();
			},
			// 表格每页数量切换
			pageSizeChange(pagesize) {
				this.pageIndex = 1;
				this.pageSize = pagesize;
				this.getData();
			},

      //处理申述
      appealCheck(row, type, index) {
        let result = 1;
        if((row.appealUserId == row.buyUserId && type == 2) || (row.appealUserId == row.sellUserId && type == 1)){
          result = 2;
        }
        let para = {
          userAppealId: row.appealId,
          status: result
        }
        appealCheck(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                if (this.status == 0) {
                  this.tableData.splice(index, 1);
                } else {
                  this.getData();
                }
                this.$Message.success('提交成功!');
              } else {
                this.$Message.error(res.data.resultMsg);
              }
            } else {
              if (res.data.resultMsg) {
                this.$Message.error(res.data.resultMsg);
              }
            }
          } else {
            this.$Message.error('接口读取失败!');
          }
        });
      },
			handelCancel() {
				this.viewModal = false;
			}
		},
		computed: {

		},
		mounted() {
			this.getData();
		},
    filters: {
      formatDate(time) {
        var date = new Date(time);
        return formatDate(date, "yyyy-MM-dd hh:mm");
      }
    }
	};
</script>
<style scoped="scoped" lang="less">
.mt8{
	span {
			text-align: right;
			vertical-align: middle;
			font-size: 12px;
			display: inline-block;
			color: #495060;
			line-height: 1;
			padding: 6px 12px 6px 0px;
			box-sizing: border-box;
			width: 180px;
		}
		.width180 {
			width: 180px;
		}
}
</style>
