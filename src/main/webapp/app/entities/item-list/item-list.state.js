(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('item-list', {
            parent: 'entity',
            url: '/item-list?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'marketlistApp.itemList.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/item-list/item-lists.html',
                    controller: 'ItemListController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('itemList');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('item-list-detail', {
            parent: 'entity',
            url: '/item-list/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'marketlistApp.itemList.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/item-list/item-list-detail.html',
                    controller: 'ItemListDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('itemList');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ItemList', function($stateParams, ItemList) {
                    return ItemList.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'item-list',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('item-list-detail.edit', {
            parent: 'item-list-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/item-list/item-list-dialog.html',
                    controller: 'ItemListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ItemList', function(ItemList) {
                            return ItemList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('item-list.new', {
            parent: 'item-list',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/item-list/item-list-dialog.html',
                    controller: 'ItemListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('item-list', null, { reload: true });
                }, function() {
                    $state.go('item-list');
                });
            }]
        })
        .state('item-list.edit', {
            parent: 'item-list',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/item-list/item-list-dialog.html',
                    controller: 'ItemListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ItemList', function(ItemList) {
                            return ItemList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('item-list', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('item-list.delete', {
            parent: 'item-list',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/item-list/item-list-delete-dialog.html',
                    controller: 'ItemListDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ItemList', function(ItemList) {
                            return ItemList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('item-list', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
