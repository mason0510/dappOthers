
// let base = process.env.NODE_ENV === 'development' ? 'http://apigateway.bitane.io': "http://apigateway.bitane.io";
// let base = process.env.NODE_ENV === 'development' ? 'http://47.52.202.194:8777': "http://47.52.202.194:8777";
// let base = process.env.NODE_ENV === 'development' ? 'http://183.129.150.2:28777': "http://183.129.150.2:28777";
let base = process.env.NODE_ENV === 'development' ? 'http://api.happy.exchange': "http://api.happy.exchange";

export const baseUrl = base;
