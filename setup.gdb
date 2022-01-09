define javadebug
    handle SIGUSR1 nostop pass
    handle SIGSEGV nostop pass
    handle SIGILL nostop pass
    handle SIGQUIT nostop pass
end

javadebug