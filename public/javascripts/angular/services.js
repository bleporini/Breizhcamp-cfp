'use strict';

/* Services */

var Services = angular.module('breizhCampCFP.services', ['ngResource', 'ngCookies']);

Services.factory('UserService', ['$http', '$log', '$location', '$cookieStore', function(http, logger, location, $cookieStore) {

        // Service pour gérer les utilisateurs
        function UserService(http, logger) {
            var userdata;
            var authenticated;
            var admin;

            // Fonction de login
            this.isLogged = function(success, error) {

                logger.info("Appel isLogged (/userLogged)");

                http({
                    method: 'GET',
                    url: '/userLogged'
                }).success(function(data, status, headers, config) {

                    $cookieStore.put('userData', data);
                    userdata = data;
                    authenticated = true;
                    if (userdata.admin)
                        admin = true;
                    logger.info('routage vers le dashboard');
                    success();
                }).error(function(data, status, headers, config) {
                    logger.info('code http de la réponse : ' + status);
                    logger.info('routage vers la page de login');
                    authenticated = false;
                    error();
                });
            };

            this.logout = function() {
                var user = this.getUserData();
                logger.info("deconnexion de " + user.email);

                http({
                    method: 'GET',
                    url: '/logout'
                }).success(
                        function(data, status, headers, config) {
                            // Suppression du cookie.
                            $cookieStore.remove('userData');
                            userdata = null;
                            authenticated = false;
                            admin = null;
                            location.url("/login");
                        })

            };

            // Fonction de login
            this.login = function(user, route, failledCallBack) {

                logger.info("Tentative d'authentification de " + user);

                http({
                    method: 'POST',
                    url: '/login',
                    data: user
                }).success(function(data, status, headers, config) {
                    logger.info(status);
                    logger.info(data);
                    $cookieStore.put('userData', data);

                    userdata = data;
                    authenticated = true;
                    if (userdata.admin)
                        admin = true;
                    logger.info('routage vers le dashboard');
                    location.url(route);

                }).error(function(data, status, headers, config) {
                    logger.info('code http de la réponse : ' + status);
                    logger.info(data);
                    failledCallBack(data);
                });
            };

            // Getters
            this.getUserData = function() {

                if (!userdata) {
                    // la méthode get renvoie un Object ou undefined
                    if (!$cookieStore.get('userData')) {
                        userdata = $cookieStore.get('userData');
                    }
                }
                return userdata;
            };


            this.isAuthenticated = function() {
                if (!authenticated) {
                    // Conversion en booleén (double not)
                    authenticated = !!this.getUserData();
                }
                return authenticated;
            };

            this.isAdmin = function() {
                if (admin == null && this.getUserData() != null) {
                    admin = this.getUserData().admin;
                }
                return admin;
            };
        }
        // instanciation du service
        return new UserService(http, logger);
    }]);

Services.factory('AccountService', function($resource) {
    function AccountService($resource) {
        this.getUser = function() {
            //return $resource('/settings/user/:id').get({id:idUser});
            return $resource('/userLogged').get();
        }

        this.getLinkType = function() {
            return $resource('/settings/link/types').query();
        }
    }

    return new AccountService($resource);
});

Services.factory('ProfilService', function($resource) {
    function ProfilService($resource) {
        this.getProposals = function(userId) {
            return $resource('/user/:userId/proposals').query({userId: userId});
        };

        this.getDrafts = function(userId) {
            return $resource('/user/:userId/drafts').query({userId: userId});
        };

        this.getProposalsAccepted = function(userId) {
            return $resource('/user/:userId/proposals/A').query({userId: userId});
        };

        this.getProposalsRefused = function(userId) {
            return $resource('/user/:userId/proposals/R').query({userId: userId});
        };

        this.getProposalsWait = function(userId) {
            return $resource('/user/:userId/proposals/W').query({userId: userId});
        };
        this.getUser = function(idUser) {
            return $resource('/user/:id').get({id: idUser});
        }
    }

    return new ProfilService($resource);
});

Services.factory('ProposalService', function($resource) {
    return $resource('/proposal/:id', {});
});

Services.factory('SubmitProposalService', function($resource) {
    return $resource('/proposal/submit/:id', {});
});


Services.factory('AllProposalService', function($resource) {
    return $resource('/proposal/all', {});
});

Services.factory('CreneauxService', function($resource) {
    return $resource('/format/:id', {});
});

Services.factory('DynamicFieldsService', function($resource) {
    return $resource('/dynamicfield/:id', {});
});


Services.factory('EventService', function($resource) {
      return $resource('/event/:id', {});
});


Services.factory('VoteService', function($resource, $http, $log) {
    function VoteService($resource, $http, $log) {

        this.getVote = function() {
            return $resource('/admin/vote', {}).get();
        }
    }
    return new VoteService($resource, $http, $log);
});

Services.factory('StatService', function($resource) {
    function StatService($resource) {
        this.getProposalStat = function() {
            return $resource('/proposalStat').get();
        }
    }
    return new StatService($resource);
});


Services.factory('ManageUsersService', function($resource) {
    return $resource('/admin/users/get', {});
});

Services.factory('PasswordService', function() {

    function PasswordService() {

        this.getPasswordStrength = function(H) {
            var A, B, C, D, E, F, G, I;
            D = H.length;
            if (D > 5) {
                D = 5;
            }
            F = H.replace(/[0-9]/g, "");
            G = H.length - F.length;
            if (G > 3) {
                G = 3;
            }
            A = H.replace(/\W/g, "");
            C = H.length - A.length;
            if (C > 3) {
                C = 3;
            }
            B = H.replace(/[A-Z]/g, "");
            I = H.length - B.length;
            if (I > 3) {
                I = 3;
            }
            E = ((D * 10) - 20) + (G * 10) + (C * 15) + (I * 10);
            if (E < 0) {
                E = 0;
            }
            if (E > 100) {
                E = 100;
            }
            return E;
        };

        this.randomPassword = function() {
            var $max, $num, $temp, chars, i, ret, size;
            chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$_+";
            size = 15;
            i = 1;
            ret = "";
            while (i <= size) {
                $max = chars.length - 1;
                $num = Math.floor(Math.random() * $max);
                $temp = chars.substr($num, 1);
                ret += $temp;
                i++;
            }
            return ret;
        };

    }

    return new PasswordService();

});