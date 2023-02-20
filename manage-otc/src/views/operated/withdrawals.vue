<template>
	<div>
		<Row :gutter="10">
      <Card>
				<p slot="title" style="height: 40px;line-height: 35px">
          <span>
            <Select  v-model="status" placeholder="放币状态" style="width: 120px">
              <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </span>
          <span><Input v-model="mobile"   placeholder="请输入申请人手机号 " style="width: 200px;" /></span>
					<span><Button  style="width:90px;"  icon="ios-search" type="ghost" @click="handleSearch">搜索</Button></span>
				</p>
        <yztz-table :columns="columns" :tableData="tableData" :loading="loading" :total="total" :pageSize="pageSize"
                    :showOP="false" @changePage="changePage" @editRow="editRow" @viewRow="viewRow"
                     @selectionChange=selectionChange @pageSizeChange="pageSizeChange">
        </yztz-table>
			</Card>

      <Modal v-model="viewModal" title="详情" :mask-closable="false" class-name="big-model">
        <Form :label-width="100" :model="editForm" ref="editForm" >
          <FormItem label="申请人">
            {{editForm.userName }}
          </FormItem>
          <FormItem label="申请人电话">
            {{editForm.mobile }}
          </FormItem>
          <FormItem label="币种">
            {{editForm.currencySimpleName }}
          </FormItem>
          <FormItem label="币种数量">
            {{editForm.numberCoins }}
          </FormItem>
          <FormItem label="手续费">
            {{editForm.serviceFee }}
          </FormItem>
          <FormItem label="应放币数量">
            {{editForm.numberCoins - editForm.serviceFee }}
          </FormItem>
          <FormItem label="币种地址">
            {{editForm.currencyAddress }}
          </FormItem>
          <FormItem label="订单状态">
            {{editForm.status | capitalOutStatus}}
          </FormItem>
          <FormItem label="申请时间">
            {{editForm.createTime | formatDate}}
          </FormItem>
          <FormItem label="放币时间">
            <div v-if="editForm.status == 1">
              {{editForm.sendTime | formatDate}}
            </div>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button type="primary" @click="viewModal=false">关闭</Button>
        </div>
      </Modal>
		</Row>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
  import {capitalOutList, withdrawals} from '../../api/withdrawals';
  import {formatDate} from '../../filters/date';
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
        //放币状态
        status :0,
        statusList: [{
          label: '全部',
          value: ''
        }, {
          label: '审核中',
          value: 0
        }, {
          label: '完成',
          value: 1
        }],
        mobile:'',
        //详情
        viewModal:false,
        editForm:{},
				//table
				columns: [],
				loading: false,
				tableData: [],
				pageSize: 20,
				pageIndex: 1,
				total: 0,
			}
		},
		methods: {
			// 初始化信息
			getData() {
        this.loading = true;
        let para = {
          status: this.status,
          mobile: this.mobile,
          currentPage: this.pageIndex,
          pageSize: this.pageSize
        }
        capitalOutList(para).then((res) => {
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
                    key:'userName'
                  },
                  {
                    title: '手机号',
                    align: 'center',
                    width: 120,
                    key:'mobile'
                  },
                  {
                    title: '币种',
                    align: 'center',
                    width: 100,
                    key:'currencySimpleName'
                  },
                  {
                    title: '币种数量',
                    align: 'center',
                    width: 100,
                    key:'numberCoins'
                  },
                  {
                    title: '手续费',
                    align: 'center',
                    width: 100,
                    key:'serviceFee'
                  },
                  {
                    title: '应放币数量',
                    align: 'center',
                    width: 120,
                    render: (h, params) => {
                      const row = params.row;
                      return h('p',row.numberCoins - row.serviceFee)
                    }
                  },
                  {
                    title: '币种地址',
                    align: 'center',
                    width: 200,
                    key:'currencyAddress'
                  },
                  {
                    title: '订单状态',
                    align: 'center',
                    width: 100,
                    render: (h, params) => {
                      const row = params.row;
                      let status = ''
                      switch (row.status) {
                        case 0:
                          status = '审核中'
                          break
                        case 1:
                          status = '完成'
                          break
                        case 2:
                          status = '失败'
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
                      if (row.status == 0) {
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
                                this.executeCurrency(row, params.index);
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
                      }else{
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

      //放币处理
      executeCurrency(row, index){
        let para = {
          capitalInOutId: row.id,
        }
        withdrawals(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                this.$Message.success('提交成功!');
                if (this.status === 0) {
                  this.tableData.splice(index, 1);
                } else {
                  this.getData();
                }
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
