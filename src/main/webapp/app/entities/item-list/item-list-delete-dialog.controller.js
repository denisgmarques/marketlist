(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .controller('ItemListDeleteController',ItemListDeleteController);

    ItemListDeleteController.$inject = ['$uibModalInstance', 'entity', 'ItemList'];

    function ItemListDeleteController($uibModalInstance, entity, ItemList) {
        var vm = this;

        vm.itemList = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ItemList.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
