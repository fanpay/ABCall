from .base_command import BaseCommannd


class ViewPing(BaseCommannd):
    def execute(self):
        return "pong"
