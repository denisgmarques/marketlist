(function() {
    'use strict';

    angular
        .module('marketlistApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('market-list', {
            parent: 'entity',
            url: '/market-list?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'marketlistApp.marketList.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/market-list/market-lists.html',
                    controller: 'MarketListController',
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
                    $translatePartialLoader.addPart('marketList');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('market-list-detail', {
            parent: 'entity',
            url: '/market-list/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'marketlistApp.marketList.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/market-list/market-list-detail.html',
                    controller: 'MarketListDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('marketList');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'MarketList', function($stateParams, MarketList) {
                    return MarketList.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'market-list',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('market-list-detail.edit', {
            parent: 'market-list-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/market-list/market-list-dialog.html',
                    controller: 'MarketListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MarketList', function(MarketList) {
                            return MarketList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('market-list.new', {
            parent: 'market-list',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/market-list/market-list-dialog.html',
                    controller: 'MarketListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                createdDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('market-list', null, { reload: true });
                }, function() {
                    $state.go('market-list');
                });
            }]
        })
        .state('market-list.edit', {
            parent: 'market-list',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/market-list/market-list-dialog.html',
                    controller: 'MarketListDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MarketList', function(MarketList) {
                            return MarketList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('market-list', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('market-list.delete', {
            parent: 'market-list',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/market-list/market-list-delete-dialog.html',
                    controller: 'MarketListDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MarketList', function(MarketList) {
                            return MarketList.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('market-list', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
