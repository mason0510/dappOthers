<template>
	<div id="push">
		<Modal v-model="modal.value" title="消息推送" ok-text="提交" class="big-model " @on-visible-change="change">
			<Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80" >
				<FormItem label="消息码"  class="title">
					<Input v-model="messageCode" placeholder="请输入消息码" disabled></Input>
				</FormItem>
				<FormItem label="标题"  prop="title">
					<Input v-model="formValidate.title" placeholder="请输入标题" ></Input>
				</FormItem>
				<FormItem label="立刻发送">
                   <RadioGroup v-model="formValidate.radio">
		                <Radio :label="1">是</Radio>
		                <Radio :label="0">否</Radio>
		            </RadioGroup>
                </FormItem>
				<FormItem label="发送时间" prop="sendTime" v-if="formValidate.radio==0">
                    <DatePicker type="datetime"  placement="right" v-model="formValidate.sendTime" placeholder="请选择发送时间"></DatePicker>
                </FormItem>
                <FormItem label="分组发送">
                   <RadioGroup v-model="formValidate.group">
		                <Radio :label="1">所有用户 </Radio>
		                <Radio :label="2">指定用户</Radio>
		            </RadioGroup>
                </FormItem>
				<FormItem label="接收人" prop="userIdList" v-if="formValidate.group==2">
					<Select v-model="formValidate.userIdList" filterable remote multiple :remote-method="remoteMethod" :loading="loading">
						<Option v-for="(option, index) in userlist" :value="option.userId" :key="index">{{option.account}}({{option.userName}})</Option>
					</Select>
				</FormItem>
				<FormItem label="内容" prop="messageContent">
					<Input v-model="formValidate.messageContent" type="textarea" :rows="4" placeholder="请输入内容"></Input>
				</FormItem>
			</Form>
			<div slot="footer">
				<Button type="primary" :loading="btnloading" @click="handleSubmit('formValidate')">推送</Button>
	            <Button type="ghost" @click="handleReset('formValidate')" style="margin-left: 8px">重置</Button>
			</div>
		</Modal>
	</div>
</template>

<script>
	import { userlistaccount } from '../../api/user'
	import {jpushMessage} from '../../api/push'
	export default {
		data() {
			return {
				btnloading:false,
				formValidate: {
					messageCode:'',
					userIdList: [], //选中用户
					messageContent: '', //推送内容
					sendTime:new Date(), //推送时间
					radio:1,
					group :1,
					title:''
				},
				loading: false,
				userlist: [], //搜索时 用户列表
				ruleValidate: {
					userIdList: [{
						required: true,
						type: 'array',
						min: 1,
						message: '请选择接收人',
						trigger: 'change'
					}],
					title: [{
						required: true,
						message: '请输入标题',
						trigger: 'blur'
					}],
					 sendTime: [
                        { required: true, type: 'date', message: '请选择发送时间', trigger: 'change' }
                    ],
					messageContent: [{
						required: true,
						message: '请输入内容',
						trigger: 'blur'
					}],
				}
			}
		},
		props: {
			modal: { //是否打开
				type: Object,
				default: function() {
					return {
						value: false
					}
				}
			},
			refType: { //类型
				type: [Number, String],
				default: ''
			},
			regionCode: { //regionCode
				type: [Number, String],
				default: ''
			},
			messageCode: { //regionCode
				type: [Number, String],
				default: ''
			},
			id: { //id
				type: [Number, String],
				default: ''
			},
			title: { //title
				type: [Number, String],
				default: ''
			},
			others: { //其他信息
				type: Object,
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
			//Model 处理
			handleSubmit() {
				this.$refs['formValidate'].validate((valid) => {
					if(valid) {
						this.btnloading=true;
						 this.$Modal.confirm({
						 	title: '确认操作',
		                    content: '确认推送？',
		                    onOk: () => {
						this.formValidate.refId=this.id;
						this.formValidate.refType=this.refType;
						this.formValidate.regionCode=this.regionCode
						this.formValidate.messageCode=this.messageCode
						if(this.formValidate.radio==1){
							this.formValidate.sendTime=new Date();
						}
						jpushMessage(this.formValidate).then((res) => {
							this.btnloading=false;
							if(res.status == 200) {
								if(res.data.success == true) {
									if(res.data.data) {
										this.$Message.success('推送成功!');
										this.modal.value = false;
									} else {
										this.$Message.error(res.data.resultMsg);
									}
								} else {
									if(res.data.resultMsg) {
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
				})
			},
			change(){
				this.formValidate={
					radio:1,
					group :1,
					title:this.title,
					messageCode:'',
					userIdList: [], //选中用户
					messageContent: '', //推送内容
					sendTime: new Date() //推送时间
				}
				this.$refs['formValidate'].resetFields();
			},
            handleReset (name) {
                this.$refs[name].resetFields();
            },
		}
	}
</script>

<style lang="less" scoped="scoped">
	.title{
		.ivu-input-type{
			width: 187px;
		}
	}
</style>