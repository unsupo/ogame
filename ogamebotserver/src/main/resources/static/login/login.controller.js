(function () {
    'use strict';

    angular
        .module('app')
        .controller('LoginController', LoginController);

    LoginController.$inject = ['$state', 'AuthenticationService', 'UIHelper'];
    function LoginController($state, AuthenticationService, UIHelper) {
        var vm = this;

        vm.login = login;

        (function initController() {
            // reset login status
            AuthenticationService.ClearCredentials();
        })();

        function login() {
            vm.dataLoading = true;
            AuthenticationService.Login(vm.username, vm.password, function (response) {
                if (response.success) {
                    AuthenticationService.SetCredentials(vm.username, vm.password);
                    // $location.path('/');
                    $state.go('main.dashboard',{}, {reload: true});
                } else {
                    // FlashService.Error(response.message);
                    UIHelper.showToast("Invalid Credentials","Please Enter Correct Credentials:\n"+response.message);
                    vm.dataLoading = false;
                }
            });
        };
    }

})();