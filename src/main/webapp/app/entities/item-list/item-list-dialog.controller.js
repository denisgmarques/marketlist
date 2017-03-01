(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .controller('ItemListDialogController', ItemListDialogController);

    ItemListDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ItemList', 'MarketList'];

    function ItemListDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ItemList, MarketList) {
        var vm = this;

        vm.itemList = entity;
        vm.clear = clear;
        vm.save = save;
        vm.marketlists = MarketList.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.itemList.id !== null) {
                ItemList.update(vm.itemList, onSaveSuccess, onSaveError);
            } else {
                ItemList.save(vm.itemList, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('marketlistApp:itemListUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
