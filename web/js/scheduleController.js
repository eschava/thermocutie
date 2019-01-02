app.controller('ScheduleController', function ($scope, $timeout, $window, $mdDialog, ScheduleService, TemperatureModeService) {
    $scope.back = function() {$window.history.back();}

    Array.prototype.flatMap = function(lambda) {
        return Array.prototype.concat.apply([], this.map(lambda));
    };

    var timeToMinutes = function(t) {
        return parseInt(t.split(':')[0]) * 60 + parseInt(t.split(':')[1])
    };

    var minutesToTime = function(m) {
        var hours = Math.floor(m / 60);
        var mins = m % 60;
        if (hours < 10) hours = "0" + hours;
        if (mins < 10) mins = "0" + mins;
        return hours + ":" + mins;
    };

    var roundMinutes = function(m) {
        return roundToMinutes * Math.round(m / roundToMinutes);
    }

    var minutesInDay = 1440;
    var roundToMinutes = 10;

    var GetScreenCoordinates = function(obj) {
            var p = {};
            p.x = obj.offsetLeft;
            p.y = obj.offsetTop;
            while (obj.offsetParent) {
                p.x = p.x + obj.offsetParent.offsetLeft;
                p.y = p.y + obj.offsetParent.offsetTop;
                if (obj == document.getElementsByTagName("body")[0]) {
                    break;
                }
                else {
                    obj = obj.offsetParent;
                }
            }
            return p;
        }

    var week = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

    var getDayNumbers = function(daysInterval) {
        return daysInterval.split(",")
                .map(function(days) {return days.split("-", 2);})
                .flatMap(function(pair) {
                    if (pair.length == 1) return [week.indexOf(pair[0])];
                    else {
                        var start = week.indexOf(pair[0]);
                        var end = week.indexOf(pair[1], start);
                        var result = [];
                        for (var i = start; i <= end; i++)
                            result.push(i);
                        return result;
                    }
                });
    }

    var simplifyDays = function(days) {
        var result = "";
        var split = days.split(",");

        var intervalStart = -1;
        var intervalStartDay = null;
        var intervalEnd = -1;
        var intervalEndDay = null;
        for (var i = 0; i < split.length; i++) {
            var day = split[i];
            if (day == '') continue;

            var dayNumber = week.indexOf(day);
            if (intervalEnd < 0 || intervalEnd < dayNumber - 1) {
                if (intervalEnd >= 0) {
                    result += (result.length > 0 ? "," : "") +
                        (intervalStartDay != intervalEndDay ? intervalStartDay + '-' + intervalEndDay : intervalEndDay);
                }
                intervalStart = dayNumber;
                intervalStartDay = day;
            }

            intervalEnd = dayNumber;
            intervalEndDay = day;
        }

        result += (result.length > 0 ? "," : "") +
            (intervalStartDay != intervalEndDay ? intervalStartDay + '-' + intervalEndDay : intervalEndDay);
        return result;
    }

    $scope.schedule = [];
    var scheduleName = null;
    $scope.daySets = {};
    ScheduleService.query({name: 'default'}, function(schedule) {
        scheduleName = schedule.name;
        // expand schedule with grouped days to schedule with separate days
        schedule.daySets.forEach(function(daySet) {
            $scope.daySets[daySet.name] = daySet;
            getDayNumbers(daySet.days).forEach(function(dayNumber, i) {
                $scope.schedule[dayNumber] = {name: week[dayNumber], set: daySet.name, periods: daySet.periods, disabled : i > 0};
            });
        });
    })

    var modesMap = {};
    $scope.modes = TemperatureModeService.query(function() {
        $scope.modes.forEach(function(mode) {modesMap[mode.name] = mode;})
    });

    $scope.update = function() {
        var daySets = Object.values($scope.daySets);

        // update daySets.days
        daySets.forEach(function(daySet) {daySet.days = "";})
        $scope.schedule.forEach(function(day) {
            $scope.daySets[day.set].days += day.name + ",";
        });
        daySets.forEach(function(daySet, index) {
            daySet.days = simplifyDays(daySet.days);
            if (daySet.days == 'null')
                daySets.splice(index, 1);
        })

        var schedule = {name: scheduleName, daySets: daySets};
        ScheduleService.updateWeekSchedule(schedule);
    }

    $scope.getStartDayColor = function(number) {
        var prevDay = $scope.schedule[number - 1]
            ? $scope.schedule[number - 1]
            : $scope.schedule[$scope.schedule.length - 1];
        var mode = prevDay.periods[prevDay.periods.length - 1].mode;
        return $scope.getModeColor(mode);
    }

    $scope.getModeColor = function(name) {
        var mode = modesMap[name];
        return mode ? mode.color : 'orange';
    }

    $scope.getPeriodTitle = function(day, period, number) {
        return period.start + " - " + (number < day.periods.length - 1 ? day.periods[number+1].start : "24:00");
    }

    $scope.onModeChanged = function(day, prevSet) {
        var setName = day.set;
        if (setName == '<new>') {
            var confirm = $mdDialog.prompt()
                .title('Input new set name')
                .placeholder('Name')
                .ok('Create')
                .cancel('Cancel');

            $mdDialog.show(confirm).then(function(newName) {
                var mode = $scope.modes[0].name;
                var daySet = {name: newName, periods: [{mode: mode, start: '12:00'}]};
                $scope.daySets[daySet.name] = daySet;
                day.set = daySet.name;
                day.periods = daySet.periods;
            }, function() {
                day.set = prevSet;
            });
        } else {
            var set = $scope.daySets[setName];
            day.periods = set.periods;

            // TODO: check if there are days having prevSet left

//            // enable only first day using this set
//            var disabled = false;
//            $scope.schedule.forEach(function(day) {
//                if (day.set == set) {
//                    day.disabled = disabled;
//                    disabled = true;
//                }
//            });
        }
    }

    var scale = $window.innerWidth / minutesInDay / 2;
    var resizerWidth = 5;

    $scope.getWidth = function(day, index, period) {
        var start = period != null ? timeToMinutes(period.start) : 0;
        var nextStart = index < day.periods.length - 1 ? timeToMinutes(day.periods[index + 1].start) : minutesInDay;

        return (nextStart - start) * scale - (index >= 0 ? resizerWidth : 0);
    }

    var pressed = false;
    var startX = 0;
    var periodToChange = null;
    var startTime = 0;
    var startPrevTime = 0;
    var startNextTime = 0;
    var index = -1;

    $scope.handleDown = function(event, period, prevPeriod, nextPeriod) {
        pressed = true;
        startX = event.type == 'touchstart' ? event.originalEvent.touches[0].startX : event.pageX;
        periodToChange = period;
        startTime = timeToMinutes(period.start);
        startPrevTime = prevPeriod != null ? timeToMinutes(prevPeriod.start) : 0;
        startNextTime = nextPeriod != null ? timeToMinutes(nextPeriod.start) : minutesInDay;
        event.preventDefault();

        $scope.showTooltip(event, period); // for touch devices
    };

    $scope.handleUp = function() {
        if (pressed) {
            pressed = false;
            $scope.hideTooltip();
        }
    };

    $scope.$watch('mouseDown', function(down) {
        if (!down) $scope.handleUp();
    });

    $scope.$watch('mouseOver', function(over) {
        if (!over) $scope.handleUp();
    });

    $scope.handleMove = function(event) {
        if (pressed) {
            var x = event.type == 'touchmove' ? event.changedTouches[0].pageX : event.pageX;
            var widthChange = x - startX;
            var newTime = roundMinutes(startTime + widthChange / scale);

            if (newTime >= startPrevTime + 30 && newTime <= startNextTime - 30) { // min interval is 30 minutes
                periodToChange.start = minutesToTime(newTime);
                $scope.showTooltip(event, periodToChange);
            }
        }
    };

    var selectedPeriod = null;
    var selectedDay = null;
    var selectedPeriodIndex = null;

    $scope.openMenu = function(period, day, periodIndex) {
        selectedPeriod = period;
        selectedDay = day;
        selectedPeriodIndex = periodIndex;

        var menu = document.getElementById('changeModeMenu');
        var c = GetScreenCoordinates(event.target);
        menu.style.left = c.x;
        menu.style.top = c.y;

        event.stopPropagation();

        $scope.currentModeName = period.mode;

        $timeout(function(){
            angular.element(menu).triggerHandler('click');
        }, 300);
    };

    var waitingSecondClick = false;
    $scope.handleTouch = function(period, day, periodIndex) {
        if (waitingSecondClick) {
            $scope.openMenu(period, day, periodIndex);
        } else {
            waitingSecondClick = true;
            $timeout(function(){
                waitingSecondClick = false;
            }, 500);
        }
    };

    $scope.chooseMode = function(mode) {
        var modeName = mode.name;
        selectedPeriod.mode = modeName;
        selectedPeriod = null;

        var prevModeName = selectedPeriodIndex > 0 ? selectedDay.periods[selectedPeriodIndex - 1].mode : null;
        var nextModeName = selectedPeriodIndex + 1 < selectedDay.periods.length ? selectedDay.periods[selectedPeriodIndex + 1].mode : null;

        if (modeName == prevModeName && modeName == nextModeName) {
            if (window.confirm("Same both. Combine?")) {
                selectedDay.periods.splice(selectedPeriodIndex + 1, 1);
                selectedDay.periods.splice(selectedPeriodIndex, 1);
            }
        } else if (modeName == prevModeName) {
            if (window.confirm("Same prev. Combine?")) {
                selectedDay.periods.splice(selectedPeriodIndex, 1);
            }
        } else if (modeName == nextModeName) {
            if (window.confirm("Same next. Combine?")) {
                selectedDay.periods.splice(selectedPeriodIndex + 1, 1);
            }
        }
    };

    $scope.renameSet = function(set) {
        var oldName = set.name;
        var confirm = $mdDialog.prompt()
            .title('Input new set name')
            .placeholder('Name')
            .initialValue(oldName)
            .ok('Rename')
            .cancel('Cancel');

        $mdDialog.show(confirm).then(function(newName) {
            set.name = newName;

            // recreate $scope.daySets
            var daySets = Object.values($scope.daySets);
            $scope.daySets = {};
            daySets.forEach(function(daySet) {
                $scope.daySets[daySet.name] = daySet;
            });

            // update schedule
            $scope.schedule.forEach(function(day) {
                if (day.set == oldName)
                    day.set = newName;
            });
        });
    };

    $scope.splitPeriod = function() {
        var periodStartTime = timeToMinutes(selectedPeriod.start)
        var endTime = selectedPeriodIndex + 1 < selectedDay.periods.length ? timeToMinutes(selectedDay.periods[selectedPeriodIndex + 1].start) : minutesInDay;

        var splitPeriod = angular.copy(selectedPeriod);
        splitPeriod.start = minutesToTime(roundMinutes((periodStartTime + endTime) / 2));

        selectedDay.periods.splice(selectedPeriodIndex + 1, 0, splitPeriod);
    }

    $scope.timeTooltipVisible = false;
    $scope.timeTooltipText = "";

    $scope.showTooltip = function(event, period) {
        $scope.timeTooltipVisible = true;
        $scope.timeTooltipText = period.start;

        var c = GetScreenCoordinates(event.target); // TODO: sometime target is not splitter
        var tooltipButton = document.getElementById('timeTooltipButton');
        tooltipButton.style.left = c.x;
        tooltipButton.style.top = c.y;
        var tooltip = document.getElementById('timeTooltip');
        if (tooltip) {
            tooltip.style.left = c.x;
            tooltip.style.top = c.y + 48;
        }
    }

    $scope.hideTooltip = function() {
        if (!pressed)
            $scope.timeTooltipVisible = false;
    }
});