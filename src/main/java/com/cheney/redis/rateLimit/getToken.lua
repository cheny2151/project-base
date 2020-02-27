-- getToken
local last_time = redis.call('HGET', 'last_time');
local permits = redis.call('HGET', 'permits');
local rate = redis.call('HGET', 'rate');
local max_permits = redis.call('HGET', 'max_permits');
-- 解决随机函数
redis.replicate_commands();
local cur_time = redis.call('time')[1];

local expect_permits = max_permits;

if (last_time ~= nil) then
    -- 计算间隔时间可新增的令牌数
    local add_permits = (cur_time - last_time) * rate;
    if (add_permits > 0) then
        redis.call('HSET', 'last_time', cur_time);
    end
    expect_permits = math.min(add_permits + permits, max_permits);
else
    redis.call('HSET', 'last_time', cur_time)
end

if expect_permits < ARVG[1] then
    redis.call('HSET', 'permits', expect_permits);
    return -1
else
    redis.call('HSET', 'permits', expect_permits - ARVG[1]);
    return 1
end