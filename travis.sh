#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
    mvn clean install sonar:sonar
else
    mvn clean install sonar:sonar -Dsonar.analysis.mode=preview \
              -Dsonar.github.pullRequest=$TRAVIS_PULL_REQUEST \
              -Dsonar.github.repository=$TRAVIS_REPO_SLUG \
              -Dsonar.github.oauth=$GITHUB_TOKEN \
              -Dsonar.host.url=https://sonarqube.com \
              -Dsonar.login=DZ9RmFt2HnjXz0wlxHlzeu6zSv4YTXRq78wx46S1J4BK3jkZIU9OeD+19VvFgwLYC5nqFXvMIt3fIdSH5hYiRpZPjsOrnDd82V4TsbJMf49b7NtOViNCVoOz3Fwxj/ykk+ISJSKOtSlyMsUmvirD1WmudVHFsQLYMXoRTr8NFTuVKjVtXf/puqPBWjq9NLFvF0U/PHgXX/t/0dbEraRnWKXH5nLo6SzCbraFIgCSzbixvpW36YZBKQ0amauBHrZZRAVnJO0fQIQd8sVEAHWf96wjF8bttTFNPiTzHo9HkIs88YRFticRi1uJlgzVm9W4s4EpvweYdq/FZ9/ezRk9sSTD/5sHTOZ0mc8Y63jbplTs6P6unbBC3Hd2ftCxJGethlP/bEgWOh56Y5q0/43DbOV7NSdnymGck/b2tiZmtCCkXT/75Kr8zq8KHJj6SC0kgJgQFS03nlFuEYJ7Fn75DoZu6Tqch9AYZXG4ZyZb3LbAusQXoUJ/gGdwpLBxxXGhhFuHxuC0NGf4pzMIE5FgPrv+D0sKVW1hZdZv1LBACXwSf4bl3niuUpQrWb36Gejg+YS/zrpSuJ0kUodDhpAhbh9zJmEPx4W72IdXr+0eTX8YHVYyLKYEodJ538f8hJ7y8MptS5DHE+xJKOPStlPcZSuIoNHKJrFZGboFQyKfKYc=
fi
