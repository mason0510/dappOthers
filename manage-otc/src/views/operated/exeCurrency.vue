<template>
	<div>
		<Row :gutter="10">
			<Card>
<!--				<p slot="title" style="height: 40px;line-height: 35px">
          <span>
            <Select  v-model="status" placeholder="申述状态" style="width: 120px">
              <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </span>
					<span><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
				</p>-->
        <yztz-table :columns="columns" :tableData="tableData" :loading="loading" :total="total" :pageSize="pageSize"
                    :showOP="false" @changePage="changePage" @editRow="editRow" @viewRow="viewRow"
                     @selectionChange=selectionChange @pageSizeChange="pageSizeChange">
        </yztz-table>
			</Card>
      <Modal v-model="viewModal" :title="modalTitle" :mask-closable="false" class-name="big-model">
        <Form :label-width="100" :model="editForm" ref="editForm" >
          <FormItem label="申请人">
            {{editForm.nickName }}
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
          <FormItem label="下单时间">
            {{editForm.crateTime}}
          </FormItem>
          <FormItem label="付款时间">
            {{editForm.payTime}}
          </FormItem>
        </Form>
      </Modal>
		</Row>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
  import {capitalDetailList, executeCurrency} from '../../api/capital'
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
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
          status: 6,
          currentPage: this.pageIndex,
          pageSize: this.pageSize
        }
        capitalDetailList(para).then((res) => {
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
                    title: '申请人',
                    align: 'center',
                    width: 100,
                    key:'nickName'
                  },
                  {
                    title: '订单号',
                    align: 'center',
                    width: 100,
                    key:'orderNumber'
                  },
                  {
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
                  },
                  {
                    title: '订单状态',
                    align: 'center',
                    width: 100,
                    render: (h, params) => {
                      const row = params.row;
                      let status = ''
                      switch (row.status) {
                        case 1:
                          status = '未付款'
                          break
                        case 2:
                          status = '已付款'
                          break
                        case 3:
                          status = '申诉中'
                          break
                        case 4:
                          status = '已取消'
                          break
                        case 5:
                          status = '已完成'
                          break
                        case 6:
                          status = '执行发币'
                          break
                        default:
                          break;
                      }
                      return h('p', status)
                    }
                  },
                  {
                    title: '操作',
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
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
                            title: '您确定执行吗?',
                            transfer: true
                          },
                          on: {
                            'on-ok': () => {
                              this.executeCurrency(row);
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
                          }, '执行放币')
                        ])
                      ]);
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

      //放币处理
      executeCurrency(row){
        let para = {
          capitalDetailId: row.capitalDetailId,
        }
        executeCurrency(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                this.tableData.splice(index, 1);
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
