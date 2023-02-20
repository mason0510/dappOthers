<template>
    <Modal
        v-model="modal.value"
        title="个人信息"
       >
        <p  v-if="brokerInfo">
         <Form :label-width="80" >
           <FormItem label="昵称：" >
             {{brokerInfo.userName}}
           </FormItem>

           <FormItem label="手机：">
             {{brokerInfo.mobile?brokerInfo.mobile:'暂无'}}
           </FormItem>

           <FormItem label="邮箱：" >
             {{brokerInfo.email?brokerInfo.email:'暂无' }}
           </FormItem>

         </Form>
       </p>

        <p v-else>{{msg}}</p>
        <div slot="footer">
        	<Button type="primary" @click="modal.value=false">关闭</Button>
        </div>
    </Modal>
</template>
<script>
	import {brokerInfoDetail,userInfoDetail} from '../../api/user'
    export default {
    	data(){
    		return {
    			brokerInfo:'',
    			msg:''
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
        	brokerUserId:{
        		type:[String,Number]
        	},
        	userId:{
        		type:[String,Number]
        	}
        },
        watch:{
        	brokerUserId(){
        		let _this= this;
        		brokerInfoDetail({brokerUserId:this.brokerUserId}).then(function(res){
        			if(res.data.success){
        				_this.brokerInfo=res.data.data;
        			}else{
        				_this.msg=res.data.resultMsg
        			}
        		})
        	},
        	userId(){
        		let _this= this;
        		userInfoDetail({userId:this.userId}).then(function(res){
        			if(res.data.success){
        				_this.brokerInfo=res.data.data;
        			}else{
        				_this.msg=res.data.resultMsg
        			}
        		})
        	},
        }
    }
</script>

<style scoped="scoped">
	p{
		font-size: 14px;
	}
</style>
