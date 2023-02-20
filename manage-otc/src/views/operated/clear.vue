<template>
	<div>
		<Row :gutter="10">
			<Card>

				<p slot="title" style="height: 40px;line-height: 35px">
					<span ><Button  style="width:150px;" type="primary" @click="showModal1()">清空资金密码错误次数</Button></span>
				</p>
        <p slot="title" style="height: 40px;line-height: 35px">
          <span ><Button  style="width:150px;" type="primary" @click="showModal2()">清空订单取消次数</Button></span>
        </p>

			</Card>
		</Row>
    <Modal v-model="clearCipherModal" title="清空资金密码错误次数" :mask-closable="false">
      <Form ref="cipherForm" :model="cipherForm" :rules="ruleValidate1" :label-width="80">
        <FormItem label="手机号码" prop="userIdList">
          <Select v-model="cipherForm.userIdList" filterable multiple remote :remote-method="remoteMethod1" :loading="loading">
            <Option v-for="(option, index) in userlist1" :value="option.userId" :key="index">{{option.account}}({{option.userName}})</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="primary" @click="clearCipher('cipherForm')">确定</Button>
        <Button type="ghost" @click="clearCipherModal=false" style="margin-left: 8px">取消</Button>
      </div>
    </Modal>
    <Modal v-model="clearTradeModal" title="清空订单取消次数" :mask-closable="false">
      <Form ref="tradeForm" :model="tradeForm" :rules="ruleValidate2" :label-width="80">
        <FormItem label="手机号码" prop="userIdList">
          <Select v-model="tradeForm.userIdList" filterable multiple remote :remote-method="remoteMethod2" :loading="loading">
            <Option v-for="(option, index) in userlist2" :value="option.userId" :key="index">{{option.account}}({{option.userName}})</Option>
          </Select>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="primary" @click="clearTrade('tradeForm')">确定</Button>
        <Button type="ghost" @click="clearTradeModal=false" style="margin-left: 8px">取消</Button>
      </div>
    </Modal>
	</div>
</template>

<script>
	import Row from "../../../node_modules/iview/src/components/grid/row.vue";
	import { clearCipherCount, clearTradeCount } from '../../api/clear';
  import { userlistaccount } from '../../api/user';
	export default {
		components: {
			'Row': Row
		},
		data() {
			return {

        clearCipherModal:false,
        clearTradeModal:false,
        btnloading:false,

        cipherForm: {
          userIdList: [], //选中用户
        },
        tradeForm: {
          userIdList: [], //选中用户
        },
        userlist1: [],
        userlist2: [],

				//table
				columns: [],
				loading: false,

        ruleValidate1: {
          userIdList: [{
            required: true,
            type: 'array',
            min: 1,
            message: '请选择用户',
            trigger: 'change'
          }]
        },
        ruleValidate2: {
          userIdList: [{
            required: true,
            type: 'array',
            min: 1,
            message: '请选择用户',
            trigger: 'change'
          }]
        }
			}
		},
		methods: {
      remoteMethod1(query) {
        let _this = this;
        if(query !== '') {
          this.loading = true;
          userlistaccount({
            keywords: query
          }).then(function(res) {
            _this.loading = false;
            _this.userlist1 = res.data.data
          })
        } else {
          _this.userlist1 = [];
        }
      },
      remoteMethod2(query) {
        let _this = this;
        if(query !== '') {
          this.loading = true;
          userlistaccount({
            keywords: query
          }).then(function(res) {
            _this.loading = false;
            _this.userlist2 = res.data.data
          })
        } else {
          _this.userlist2 = [];
        }
      },

      //打开清空资金密码错误次数界面
      showModal1(){
        this.$refs['cipherForm'].resetFields();
				this.clearCipherModal=true;
			},
      //打开清空用户订单取消次数界面
      showModal2(){
        this.$refs['tradeForm'].resetFields();
        this.clearTradeModal=true;
      },
      //清空资金密码错误次数
      clearCipher() {
        if (this.cipherForm.userIdList.length <= 0) {
          this.$Message.error("请选择用户");
          return;
        }
        this.btnloading = true;
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认清空资金密码错误次数吗？',
          onOk: () => {
            clearCipherCount(this.cipherForm).then((res) => {
              this.btnloading = false;
              if (res.status == 200) {
                if (res.data.success == true) {
                  this.$Message.success('清空成功!');
                  this.clearCipherModal = false;
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
      },
      //清空订单取消次数
      clearTrade() {
        if (this.tradeForm.userIdList.length <= 0) {
          this.$Message.error("请选择用户");
          return;
        }
        this.btnloading = true;
        this.$Modal.confirm({
          title: '确认操作',
          content: '确认清空订单取消次数吗？',
          onOk: () => {
            clearTradeCount(this.tradeForm).then((res) => {
              this.btnloading = false;
              if (res.status == 200) {
                if (res.data.success == true) {
                  this.$Message.success('清空成功!');
                  this.clearTradeModal = false;
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
	};
</script>
