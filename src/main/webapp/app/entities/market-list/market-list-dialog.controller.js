(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .controller('MarketListDialogController', MarketListDialogController);

    MarketListDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MarketList', 'ItemList'];

    function MarketListDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MarketList, ItemList) {
        var vm = this;

        vm.marketList = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.itemlists = ItemList.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.marketList.id !== null) {
                MarketList.update(vm.marketList, onSaveSuccess, onSaveError);
            } else {
                MarketList.save(vm.marketList, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('marketlistApp:marketListUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createdDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
