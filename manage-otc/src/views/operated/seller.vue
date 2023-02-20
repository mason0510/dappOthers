<template>
	<div>
		<Row :gutter="10">
			<Card>

				<p slot="title" style="height: 40px;line-height: 35px">
					<span ><Button  style="width:90px;" type="primary" @click="addSeller()">添加商户</Button></span>
				</p>
				<yztz-table :columns="columns" :tableData="tableData" :loading="loading" :total="total" :pageSize="pageSize" :showOP="false" @changePage="changePage" @doOther='doOther' @pageSizeChange="pageSizeChange">

				</yztz-table>
			</Card>
		</Row>
    <Modal v-model="showModal" title="添加商户" :mask-closable="false">
      <Form ref="editForm" :model="editForm" :rules="ruleValidate" :label-width="80">
        <FormItem label="手机号码" prop="userIdList">
          <Select v-model="editForm.userIdList" filterable remote multiple :remote-method="remoteMethod" :loading="loading">
            <Option v-for="(option, index) in userlist" :value="option.userId" :key="index">{{option.account}}({{option.userName}})</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="primary" @click="handleSubmit('editForm')">添加</Button>
        <Button type="ghost" @click="showModal=false" style="margin-left: 8px">取消</Button>
      </div>
    </Modal>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
	import { sellerList, addSeller } from '../../api/seller'
  import { userlistaccount } from '../../api/user'
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {
			  // userIdList:[],
        showModal:false,
        btnloading:false,

        editForm: {
          userIdList: [], //选中用户
        },
        userlist: [],

				//table
				columns: [],
				loading: false,
				tableData: [],
				pageSize: 20,
				pageIndex: 1,
				total: 0,

        ruleValidate: {
          userIdList: [{
            required: true,
            type: 'array',
            min: 1,
            message: '请选择商户',
            trigger: 'change'
          }]
        }
			}
		},
		methods: {
      remoteMethod(query) {
        let _this = this;
        if(query !== '') {
          this.loading = true;
          userlistaccount({
            keywords: query
          }).then(function(res) {
            _this.loading = false;
            _this.userlist = res.data.data
          })
        } else {
          _this.userlist = [];
        }
      },
			// 初始化信息
			getData() {
				let _this = this;
				this.loading = true;
				let para = {
          currentPage: this.pageIndex,
					pageSize: this.pageSize
				}
        sellerList(para).then((res) => {
          if (res.status == 200) {
            if (res.data.success == true) {
              if (res.data.data) {
                this.columns = [{
                  title: '',
                  type: 'index',
                  width: 60,
                  align: 'center'
                },
                  {
                    title: '用户名',
                    align: 'center',
                    width: 150,
                    key: 'userName'
                  },
                  {
                    title: '邮箱',
                    align: 'center',
                    width: 150,
                    key: 'email'
                  },
                  {
                    title: '用户手机',
                    align: 'center',
                    width: 150,
                    key: 'mobile'
                  },
                  {
                    title: '操作',
                    align: 'center',
                    render: (h, params) => {
                      const row = params.row;
                      return h('div', []);
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
			//其它按钮点击事件
			doOther(currentRow, index, e) {},
      //打开新增商户界面
      addSeller(){
        this.$refs['editForm'].resetFields();
				this.showModal=true;
			},
      //添加商户
      handleSubmit() {
        this.btnloading = true;
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认添加此用户为商户吗？',
          onOk: () => {

            addSeller(this.editForm).then((res) => {
              this.btnloading = false;
              if (res.status == 200) {
                if (res.data.success == true) {
                  this.$Message.success('添加成功!');
                  this.showModal = false;
                  this.getData();
                } else {
                  if (res.data.resultMsg) {
                    this.$Message.error(res.data.resultMsg);
                  }
                }
              } else {
                this.$Message.error('接口读取失败!');
              }
            });
          }
        });
      }

		},
    mounted() {
      this.getData();
    },
	};
</script>
