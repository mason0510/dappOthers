<style lang="less">
    @import './login.less';
</style>

<template>
    <div class="login" @keydown.enter="handleSubmit">
        <div class="login-con">
            <Card :bordered="false">
                <p slot="title">
                    <Icon type="log-in"></Icon>
                    欢迎登录
                </p>
                <div class="form-con">
                    <Form ref="loginForm" :model="form" :rules="rules">
                        <FormItem prop="userName">
                            <Input v-model="form.userName" placeholder="请输入用户名">
                                <span slot="prepend">
                                    <Icon :size="16" type="person"></Icon>
                                </span>
                            </Input>
                        </FormItem>
                        <FormItem prop="password" class="mb8">
                            <Input type="password" v-model="form.password" placeholder="请输入密码">
                                <span slot="prepend">
                                    <Icon :size="14" type="locked"></Icon>
                                </span>
                            </Input>
                        </FormItem>
                          <FormItem >
                            <Checkbox v-model="form.pwdRemember">记住用户名密码</Checkbox>
                        </FormItem>
                        <FormItem>
                            <Button @click="handleSubmit" type="primary" long>登录</Button>
                        </FormItem>
                    </Form>
                </div>
            </Card>
        </div>
    </div>
</template>

<script>
import Cookies from 'js-cookie';
import {loginUser} from '../api/user';
import md5 from 'js-md5';
export default {
    data () {
        return {
            form: {
                userName: '',
                password: '',
                pwdRemember:false
            },
            rules: {
                userName: [
                    { required: true, message: '账号不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleSubmit () {
            this.$refs.loginForm.validate((valid) => {
                if (valid) {

//                    this.$store.commit('setAvator', 'https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3448484253,3685836170&fm=27&gp=0.jpg');
//                    if (this.form.userName === 'iview_admin') {
//                        Cookies.set('access', 0);
//                    } else {
//                        Cookies.set('access', 1);
//                    }

                  let pwdOpt = "www.yztz.com#1Pwd_salt@Default2+!`%Ok_here'The&End$"
                  let password = md5(pwdOpt + this.form.password);
                  console.log(password);
                  let para = {
                    "account": this.form.userName,
                    "applicationClientType": 1,
                    "applicationId": 4,
                    "authType": 0,
                    "loginMethod": 1,
                    "password": password,
                    "wechatCode": ''
                  }

                  loginUser(para).then((res) => {

                    if(res.data.code==0){
                    	if(this.form.pwdRemember){
                    		localStorage.setItem('userName',this.form.userName)
                    		localStorage.setItem('password',this.form.password)
                    	}else{
                    		localStorage.removeItem('userName')
                    		localStorage.removeItem('password')
                    	}
                    	sessionStorage.setItem('userId',res.data.data.userId)
                      Cookies.set('user', this.form.userName);
                      Cookies.set('password', this.form.password);
                      Cookies.set('token', res.data.data.token);
                      this.$router.push({
                        name: 'home_index'
                      });
                    }

                  });


                }
            });
        }
    },
    mounted(){
    	if(localStorage.getItem('userName')){
    		this.form.userName=localStorage.getItem('userName');
    		this.form.password=localStorage.getItem('password');
    	}
    }
};
</script>

<style lang="less">
.form-con{
.mb8{
	margin-bottom: 8px;
}
}
</style>
