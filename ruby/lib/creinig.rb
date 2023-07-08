require 'logger'

module Creinig
  def log
    return @logger if @logger

    level = ENV['LOG_LEVEL'] || :warn
    @logger = Logger.new(STDOUT)
    @logger.level = level
    @logger
  end
end
