(function () {
    'use strict';

    angular
        .module('app')
        .controller('RegisterController', RegisterController);

    RegisterController.$inject = ['UserService', '$location', '$rootScope','UIHelper'];
    function RegisterController(UserService, $location, $rootScope,UIHelper) {
        var vm = this;

        vm.register = register;

        function register() {
            vm.dataLoading = true;
            UserService.Create(vm.user)
                .then(function (response) {
                    if (response.success) {
                        UIHelper.showToast('Registration successful', response.message);
                        $location.path('/login');
                    } else {
                        UIHelper.showToast("Invalid Registration","Please Try Again:\n"+response.message);
                        vm.dataLoading = false;
                    }
                });
        }
    }

})();