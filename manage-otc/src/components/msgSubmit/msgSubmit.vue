<template>
	<div id="msgSubmit">
		<Modal
        v-model="modal.value"
        :title="title"
        class="big-model"
         @on-visible-change="modalChange"
        >
        <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="80">
        	<FormItem label="用户名" prop="userId"> 
         <Select
         	placeholder="请选择用户"
            v-model="formValidate.userId"
            filterable
            remote
            :remote-method="remoteMethod"
            :loading="loading">
            <Option v-for="(option, index) in userlist" :value="option.userId" :key="index">{{option.account}}({{option.userName}})</Option>
        </Select>
         </FormItem>
         <FormItem label="内容" prop="content">
        <Input v-model="formValidate.content" type="textarea" :rows="4" placeholder="请输入内容"></Input>
         </FormItem>
        <div class="mt16" v-if="uploadType">
            	<p class="mt8" v-for="(item,index) in imgList">{{index}}.{{item}}</p>
            </div>
        <p class="mt16" v-if="uploadType" style="color: crimson;">注：上传图片请勿超过3张，如图片超过3张，只会取前3张</p>
         <div class="mt8" v-if="uploadType">
            <yztz-upload-image :uploadUrl="uploadUrl"
            :uploadType="uploadType"
            @onUploaded="onUploaded" :files=files ></yztz-upload-image>
            </div>
            </Form> 
            <div slot="footer">
            	<Button type="primary" @click="ok('formValidate')">提交</Button>
            	<Button  @click="modal.value=false">取消</Button>
            </div>
    </Modal>
	</div>
</template>

<script>
	import {fileUploadUrl} from '../../api/system'
	import {userlistaccount} from '../../api/user'
	import {manageaddComment} from '../../api/comment'
	 export default {
        data () {
            return {
            	uploadUrl:fileUploadUrl,
                loading:false,
                userlist:[],
                imgList:[],
                formValidate:{
                	content:'',
                	userId:''
                },
                files:[],
                 ruleValidate: {
                    content: [
                        { required: true, message: '请输入内容', trigger: 'blur' }
                    ],
                     userId: [
                        { required: true,type: 'number', message: '请选择用户', trigger: 'change,blur' }
                    ]
                  }
            }
        },
        props:{
        	modal:{
        		type:Object,
        		default:function(){
        			return {
        				value:false
        			}
        		}
        	},
        	title:{
        		type:String,
        		default:'信息提交'
        	},
        	uploadType:{
        		type:[Number,String],
        		default:''
        	},
        	type:{
        		type:[Number,String],
        		default:''
        	},
        	id:{
        		type:[Number,String],
        		default:''
        	},
        	others:{
        		type:Object,
        	}
        },
        methods: {
        	 remoteMethod (query) {
        	 	let _this=this;
                if (query !== '') {
                    this.loading = true;
                    userlistaccount({keywords:query}).then(function(res){
                    	_this.loading = false;
                    	_this.userlist =res.data.data
                    })
                } else {
                    _this.userlist = [];
                }
            },
            ok () {
            	   this.$refs['formValidate'].validate((valid) => {
            	let _this = this;
            	if (valid) {
            	if(!_this.id){
            		_this.$Message.success('请选择用户');
            		return false
            	}
            	if(_this.type=='comment'){
            		manageaddComment({
            			content :_this.formValidate.content,
            			refId   :_this.id ,
            			refType  :_this.others.refType,
            			userId   :_this.formValidate.userId,
            		}).then(function(res){
            			if(res.data.success){
            				_this.modal.value=false;
            				_this.$Message.success('成功!');
            			}else{
            				_this.$Message.error('失败!');
            			}
            		})
            	}
            	} else {
            		return false;
                    }
            	 })
            },
          onUploaded(data){
          	this.imgList.push(data)
          },
          modalChange(){
          	this.$refs['formValidate'].resetFields();
          	this.formValidate.userId='',
          	this.formValidate.content='',
          	this.imgList=[],
          	this.files=[]
          }
        }
    }
</script>

<style lang="less" scoped="scoped">
</style>