export const state = () => ({
  user: {},//user信息
  isLogin: false,//是否登陆
});

export const mutations = {

  login(state, user) {
    state.user = user;
    state.isLogin = true
  },
  logout(state) {
    state.isLogin = false;
    state.user = {}
  },
};
