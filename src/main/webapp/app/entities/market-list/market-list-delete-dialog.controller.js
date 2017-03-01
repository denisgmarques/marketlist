(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .controller('MarketListDeleteController',MarketListDeleteController);

    MarketListDeleteController.$inject = ['$uibModalInstance', 'entity', 'MarketList'];

    function MarketListDeleteController($uibModalInstance, entity, MarketList) {
        var vm = this;

        vm.marketList = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MarketList.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
